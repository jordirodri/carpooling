package org.cabify.carpooling.web;

import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.repositories.CarRepository;
import org.cabify.carpooling.repositories.GroupRepository;
import org.cabify.carpooling.services.CarService;
import org.cabify.carpooling.services.GroupService;
import org.cabify.carpooling.services.JourneyService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
//Class used only for batch testing in local
//@SpringBootTest
public class MultipleCarsIT {

    @Autowired
    JourneyService journeyService;

    @Autowired
    CarService carService;

    @Autowired
    GroupService groupService;

    @Autowired
    CarRepository carRepository;
    @Autowired
    GroupRepository groupRepository;

    @BeforeEach
    public void cleanUp() {
        carRepository.deleteAll().subscribe();
        groupRepository.deleteAll().subscribe();
    }

    //@Test
    // @Ignore
    void testJourney() {
        Integer numCars = 10;
        Integer numGroups = 100;

        List<Car> cars = IntStream.range(0, numCars)
                .boxed()
                .map(num -> Car.builder().id(num + 1).seats((num % 6) + 1).emptySeats((num % 6) + 1).groupIds(List.of()).build())
                .collect(Collectors.toList());

        carService.cars(cars).subscribe();

        carService.findAll()
                .subscribe(car1 -> log.info("Cars just created{}", car1));

        List<Groups> groupsList = IntStream.range(0, numGroups)
                .boxed()
                .map(num -> Groups.builder().id(num + 1).people((num % 6) + 1).build())
                .collect(Collectors.toList());

        groupsList.forEach(groups2 ->
                journeyService.journey(groups2).subscribe());

        log.info("**********************************");
//        carService.findAll().subscribe(car1 -> log.info("Cars before dropoff{}", car1));
//        groupService.findAll().subscribe(groups -> log.info("Groups before dropoff{}", groups));

        log.info("**********************************");
        journeyService.locate(6).subscribe(car -> log.info("Car for group 6 located: {}", car));
        journeyService.locate(4).subscribe(car -> log.info("Car for group 4 located: {}", car));

        journeyService.dropoff(6).subscribe();
        journeyService.dropoff(4).subscribe();
        journeyService.dropoff(9).subscribe();
        journeyService.dropoff(5).subscribe();
        journeyService.dropoff(11).subscribe();
        journeyService.dropoff(13).subscribe();

        log.info("**********************************");
        carService.findAll().subscribe(car1 -> log.info("Cars {}", car1));

        groupService.findAll().subscribe(groups -> log.info("Groups {}", groups));

    }


    //@Test
    // @Ignore
    void testJourney2() {
        Integer numCars = 1000;
        Integer numGroups = 10000;

        List<Car> cars = IntStream.range(0, numCars)
                .boxed()
                .map(num -> Car.builder().id(num + 1).seats((num % 6) + 1).emptySeats((num % 6) + 1).groupIds(List.of()).build())
                .collect(Collectors.toList());

        carService.cars(cars).subscribe();

        List<Groups> groupsList = IntStream.range(0, numGroups)
                .boxed()
                .map(num -> Groups.builder().id(num + 1).people((num % 6) + 1).build())
                .collect(Collectors.toList());

        groupsList.forEach(groups2 ->
                journeyService.journey(groups2).subscribe());

        log.info("**********************************");

        log.info("**********************************");
        journeyService.locate(6).subscribe(car -> log.info("Car for group 6 located: {}", car));
        journeyService.locate(4).subscribe(car -> log.info("Car for group 4 located: {}", car));

        IntStream.range(0, 100).boxed().map(integer -> journeyService.dropoff(integer).subscribe()).collect(Collectors.toList());

        log.info("**********************************");
        carService.findAll().filter(car -> !car.getWaiting()).subscribe(car1 -> log.info("Cars {}", car1));

    }

}
