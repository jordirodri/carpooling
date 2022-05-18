package org.cabify.carpooling.services;

import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.repositories.CarRepository;
import org.cabify.carpooling.repositories.GroupRepository;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    CarRepository carRepository;

    @Mock
    GroupRepository groupRepository;

    @Spy
    @InjectMocks
    CarServiceImpl carService;

    @Test
    void given_listOfCars_when_carsIsCalled_then_dataIsCleanedAndCarsArePersisted() {

        List<Car> cars = IntStream.range(0, 10)
                .boxed()
                .map(num -> Car.builder().id(num).seats(num % 6).build())
                .collect(Collectors.toList());

        doReturn(Mono.empty()).when(carRepository).deleteAll();
        doReturn(Mono.empty()).when(groupRepository).deleteAll();
        doReturn(Flux.empty()).when(carRepository).saveAll(anyList());
        carService.cars(cars);

        verify(carRepository).deleteAll();
        verify(groupRepository).deleteAll();
        verify(carRepository).saveAll(cars);

    }

    @Test
    void findByGroupId() {
        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).waiting(true).groupIds(List.of(1, 2, 3)).build());
        cars.add(Car.builder().id(2).seats(3).waiting(true).groupIds(List.of(4, 5)).build());

        doReturn(Flux.fromIterable(cars)).when(carRepository).findAll();

        carService.findByGroupId(5)
                .as(StepVerifier::create)
                .expectNextMatches(car -> car.getId().equals(2))
                .verifyComplete();

        carService.findByGroupId(1234)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}