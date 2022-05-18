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
public class DropoffIT {

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
    void given_groupsInJourney_when_dropOff_then_theGroupIsRemovedFromCarAndDeleted() {

        //GIVEN
        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(3).waiting(true).groupIds(new ArrayList<>()).build());
        cars.add(Car.builder().id(3).seats(6).emptySeats(6).waiting(true).groupIds(new ArrayList<>()).build());

        StepVerifier.create(carService.cars(cars))
                .expectComplete()
                .verify();

        Groups group = Groups.builder().id(1).people(5).build();

        StepVerifier.create(journeyService.journey(group))
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

        //WHEN
        StepVerifier.create(journeyService.dropoff(1))
                .expectComplete()
                .verify();

        //THEN
        carService.findByGroupId(group.getId())
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        groupService.findById(group.getId())
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        carService.findById(3)
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(3)
                                && !car.getGroupIds().contains(group.getId())
                                && car.getWaiting()
                                && car.getEmptySeats().equals(6)
                )
                .expectComplete()
                .verify();
    }

    @Test
    void given_groupsAreWaiting_when_dropOff_then_waitingGroupsAreAssignedToCars() {

        List<Integer> groups1 = new ArrayList<>();
        List<Integer> groups2 = new ArrayList<>();
        List<Integer> groups3 = new ArrayList<>();

        groups1.add(1);
        groups2.add(2);
        groups3.add(3);

        List<Car> cars = new ArrayList<>();
        cars.add(Car.builder().id(1).seats(2).emptySeats(2).waiting(true).groupIds(groups1).build());
        cars.add(Car.builder().id(2).seats(3).emptySeats(3).waiting(true).groupIds(groups2).build());
        cars.add(Car.builder().id(3).seats(6).emptySeats(6).waiting(true).groupIds(groups3).build());

        StepVerifier.create(carService.cars(cars))
                .expectComplete()
                .verify();

        Groups group4 = Groups.builder().id(4).people(2).waiting(true).build();
        Groups group5 = Groups.builder().id(5).people(3).waiting(true).build();
        Groups group6 = Groups.builder().id(6).people(6).waiting(true).build();

        StepVerifier.create(journeyService.journey(group4))
                .expectComplete()
                .verify();
        StepVerifier.create(journeyService.journey(group5))
                .expectComplete()
                .verify();
        StepVerifier.create(journeyService.journey(group6))
                .expectComplete()
                .verify();

        //WHEN
        StepVerifier.create(journeyService.dropoff(1))
                .expectComplete()
                .verify();

        StepVerifier.create(journeyService.dropoff(2))
                .expectComplete()
                .verify();

        StepVerifier.create(journeyService.dropoff(3))
                .expectComplete()
                .verify();


        carService.findByGroupId(group4.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(1)
                                && car.getGroupIds().contains(group4.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();


        carService.findByGroupId(group5.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(2)
                                && car.getGroupIds().contains(group5.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();

        carService.findByGroupId(group6.getId())
                .as(StepVerifier::create)
                .expectNextMatches(car ->
                        car.getId().equals(3)
                                && car.getGroupIds().contains(group6.getId())
                                && !car.getWaiting()
                                && car.getEmptySeats().equals(0)
                )
                .expectComplete()
                .verify();

    }

}
