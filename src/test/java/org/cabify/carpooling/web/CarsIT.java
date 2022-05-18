package org.cabify.carpooling.web;

import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.services.CarService;
import org.cabify.carpooling.services.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
public class CarsIT {

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
    void given_listOfCars_when_cars_then_carsAreCleanupAndSaved() {

        List<Car> cars = IntStream.range(0, 5)
                .boxed()
                .map(num -> Car.builder().id(num).seats(num % 6).emptySeats(num % 6).groupIds(List.of()).build())
                .collect(Collectors.toList());

        carService.cars(cars)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        carService.findAll()
                .as(StepVerifier::create)
                .expectNextMatches(car -> car.getEmptySeats().equals(car.getSeats()) && car.getGroupIds().isEmpty())
                .expectNextMatches(car -> car.getEmptySeats().equals(car.getSeats()) && car.getGroupIds().isEmpty())
                .expectNextMatches(car -> car.getEmptySeats().equals(car.getSeats()) && car.getGroupIds().isEmpty())
                .expectNextMatches(car -> car.getEmptySeats().equals(car.getSeats()) && car.getGroupIds().isEmpty())
                .expectNextMatches(car -> car.getEmptySeats().equals(car.getSeats()) && car.getGroupIds().isEmpty())
                .expectComplete()
                .verify();
    }
}
