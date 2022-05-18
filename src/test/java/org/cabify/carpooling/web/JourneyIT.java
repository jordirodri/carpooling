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

@Slf4j
@SpringBootTest
public class JourneyIT {

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
    void given_group_when_journey_then_groupIsAssignedToACar() {

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

        carService.findByGroupId(group.getId())
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
    void given_group_when_journey_then_groupIsNotAssignedToACar() {

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

        carService.findByGroupId(group.getId())
                .as(StepVerifier::create)
                .expectComplete()
                .verify();


        groupService.findById(group.getId())
                .as(StepVerifier::create)
                .expectNextMatches(groups -> group.getWaiting().equals(true))
                .expectComplete()
                .verify();
    }

    @Test
    void given_2groups_when_journey_then_groupsAreAssignedToCars() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(3).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(3).seats(4).emptySeats(4).waiting(true).groupIds(new ArrayList<>()).build());

        carService.cars(cars)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group1 = Groups.builder().id(1).people(2).build();
        journeyService.journey(group1)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group2 = Groups.builder().id(2).people(3).build();
        journeyService.journey(group2)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        carService.findByGroupId(group1.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(1)
                                && car.getGroupIds().contains(group1.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();

        carService.findByGroupId(group2.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(2)
                                && car.getGroupIds().contains(group2.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();

    }

    @Test
    void given_3groups_when_journey_then_3groupsAreAssignedToCars() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(3).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(3).seats(4).emptySeats(4).waiting(true).groupIds(new ArrayList<>()).build());

        carService.cars(cars)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group1 = Groups.builder().id(1).people(2).build();
        journeyService.journey(group1)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group2 = Groups.builder().id(2).people(2).build();
        journeyService.journey(group2)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group3 = Groups.builder().id(3).people(3).build();
        journeyService.journey(group3)
                .as(StepVerifier::create)

                .expectComplete()
                .verify();

        carService.findByGroupId(group1.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(1)
                                && car.getGroupIds().contains(group1.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();

        carService.findByGroupId(group2.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(2)
                                && car.getGroupIds().contains(group2.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(1)
                )
                .expectComplete()
                .verify();

        carService.findByGroupId(group3.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(3)
                                && car.getGroupIds().contains(group3.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(1)
                )
                .expectComplete()
                .verify();

    }

    @Test
    void given_3groups_when_journey_then_2groupsAreAssignedToCars_and_1groupIsWaiting() {

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(2).seats(4).emptySeats(4).waiting(true).groupIds(new ArrayList<>()).build());

        carService.cars(cars)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group1 = Groups.builder().id(1).people(2).build();
        journeyService.journey(group1)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group2 = Groups.builder().id(2).people(2).build();
        journeyService.journey(group2)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        Groups group3 = Groups.builder().id(3).people(3).build();
        journeyService.journey(group3)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        carService.findByGroupId(group1.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(1)
                                && car.getGroupIds().contains(group1.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();

        carService.findByGroupId(group2.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(2)
                                && car.getGroupIds().contains(group2.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(2)
                )
                .expectComplete()
                .verify();

        carService.findByGroupId(group3.getId())
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        groupService.findById(group3.getId())
                .as(StepVerifier::create)
                .expectNextMatches(group -> group.getWaiting().equals(true))
                .expectComplete()
                .verify();
    }

}
