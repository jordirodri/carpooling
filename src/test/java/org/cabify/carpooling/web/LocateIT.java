package org.cabify.carpooling.web;

import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.services.CarService;
import org.cabify.carpooling.services.GroupService;
import org.cabify.carpooling.services.JourneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
public class LocateIT {

    @Autowired
    JourneyService journeyService;

    @Autowired
    CarService carService;

    @Autowired
    GroupService groupService;


    @BeforeEach
    public void cleanUp() {
        carService.deleteAll().subscribe();
        groupService.deleteAll().subscribe();
    }

    @Test
    void given_groupIsAssignedToCar_when_locate_then_CarIsReturned() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(3).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(3).seats(6).emptySeats(6).waiting(true).groupIds(new ArrayList<>()).build());

        carService.cars(cars)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group = Groups.builder().id(1).people(5).build();
        journeyService.journey(group)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        journeyService.locate(group.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(3)
                                && car.getGroupIds().contains(group.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(1)
                )
                .expectComplete()
                .verify();

        groupService.findById(group.getId())
                .as(StepVerifier::create)
                .expectNextMatches(groups -> group.getWaiting().equals(false))
                .expectComplete()
                .verify();
    }

    @Test
    void given_groupNotAssignedToCar_when_locate_then_NoContentException() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(3).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(3).seats(4).emptySeats(4).waiting(true).groupIds(new ArrayList<>()).build());

        carService.cars(cars)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group = Groups.builder().id(1).people(5).build();
        journeyService.journey(group)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        journeyService.locate(group.getId())
                .as(StepVerifier::create)
                .expectError(NoContentException.class)
                .verify();


        groupService.findById(group.getId())
                .as(StepVerifier::create)
                .expectNextMatches(groups -> group.getWaiting().equals(true))
                .expectComplete()
                .verify();
    }

    @Test
    void given_groupThatDoesNotExists_when_locate_then_NotFoundException() {

        journeyService.locate(2)
                .as(StepVerifier::create)
                .expectError(NotFoundException.class)
                .verify();
    }


}
