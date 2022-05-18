package org.cabify.carpooling.services;

import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class JourneyServiceImplTest {

    @Mock
    CarService carService;

    @Mock
    GroupService groupService;

    @Spy
    @InjectMocks
    JourneyServiceImpl journeyService;

    @Test
    void given_groupOfPeople_when_journeyIsCalledAndSeatsAreAvailableInACar_then_groupIsAssignedToCar() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(0).waiting(false).groupIds(List.of(1, 2)).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(0).waiting(false).groupIds(List.of(3, 4)).build());
        cars.add(Car.builder().id(3).seats(6).emptySeats(6).waiting(true).groupIds(new ArrayList<>()).build());

        doReturn(Flux.fromIterable(cars)).when(carService).findAll();
        doReturn(Mono.just(Groups.builder().id(1).people(2).build())).when(groupService).save(any());
        doReturn(Mono.just(Car.builder().id(3).seats(6).waiting(true).build())).when(carService).save(any());
        doReturn(Flux.empty()).when(groupService).findAll();
        Groups group = Groups.builder().id(5).people(2).waiting(true).build();

        journeyService.journey(group)
                .as(StepVerifier::create)
                .verifyComplete();

        verify(groupService, times(1)).save(any());
        verify(carService).save(Car.builder().id(3).seats(6).emptySeats(4).waiting(false).groupIds(List.of(5)).build());
    }

    @Test
    void given_groupOfPeople_when_journeyIsCalledAndSeatsAreNotAvailableInAnyCar_then_groupIsNotAssignedToCar() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(0).waiting(false).groupIds(List.of(1, 2)).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(0).waiting(false).groupIds(List.of(3, 4)).build());

        doReturn(Flux.fromIterable(cars)).when(carService).findAll();
        doReturn(Mono.just(Groups.builder().id(1).people(2).build())).when(groupService).save(any());

        Groups group = Groups.builder().id(5).people(2).waiting(true).build();

        journeyService.journey(group)
                .as(StepVerifier::create)
                .verifyComplete();

        verify(groupService).save(Groups.builder().id(5).people(2).waiting(true).build());
        verify(carService, times(0)).save(any());
    }


    @Test
    void given_groupOfPeopleInAJourney_when_dropoffIsCalled_then_groupIsRemovedFromCar() {

        List groupIds = new ArrayList();
        groupIds.add(1);
        groupIds.add(2);

        Car car = Car.builder().id(1).seats(2).emptySeats(0).waiting(false).groupIds(groupIds).build();
        doReturn(Mono.just(car)).when(carService).findByGroupId(2);
        doReturn(Flux.empty()).when(groupService).findAll();
        doReturn(Mono.just(car)).when(carService).save(any());

        Groups group = Groups.builder().id(2).people(5).waiting(false).build();
        doReturn(Mono.just(group)).when(groupService).findById(2);
        doReturn(Mono.empty()).when(groupService).delete(any());

        journeyService.dropoff(2)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(carService).save(Car.builder().id(1).seats(2).emptySeats(5).waiting(false).groupIds(List.of(1)).build());
    }

    @Test
    void given_groupOfPeopleInAJourney_when_dropoffIsCalled_then_groupIsRemovedFromCarAndCarIsEmpty() {

        List groupIds = new ArrayList();
        groupIds.add(2);

        Car car = Car.builder().id(1).seats(2).emptySeats(2).waiting(false).groupIds(groupIds).build();
        doReturn(Mono.just(car)).when(carService).findByGroupId(2);
        doReturn(Mono.just(Groups.builder().id(2).people(2).build())).when(groupService).findById(2);
        doReturn(Flux.empty()).when(groupService).findAll();
        doReturn(Mono.just(car)).when(carService).save(any());
        doReturn(Mono.empty()).when(groupService).delete(any());

        journeyService.dropoff(2)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(carService).save(Car.builder().id(1).seats(2).emptySeats(4).waiting(true).groupIds(new ArrayList<>()).build());
    }

    @Test
    void given_groupOfPeopleInAJourney_when_locateIsCalled_then_carIsFound() {

        Car car = Car.builder().id(1).seats(2).waiting(false).groupIds(List.of(2)).build();
        Groups group = Groups.builder().id(2).people(5).waiting(false).build();
        doReturn(Mono.just(car)).when(carService).findByGroupId(2);
        doReturn(Mono.just(group)).when(groupService).findById(2);

        journeyService.locate(2)
                .as(StepVerifier::create)
                .expectNext(car)
                .expectComplete()
                .verify();

    }

    @Test
    void given_groupOfPeopleNotInAJourney_when_locateIsCalled_then_carIsNotFound() {
        Groups group = Groups.builder().id(2).people(5).waiting(false).build();
        doReturn(Mono.empty()).when(carService).findByGroupId(anyInt());
        doReturn(Mono.just(group)).when(groupService).findById(2);

        journeyService.locate(2)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

    }

}