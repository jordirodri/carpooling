package org.cabify.carpooling.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.web.NoContentException;
import org.cabify.carpooling.web.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class JourneyServiceImpl implements JourneyService {

    private final GroupService groupService;

    private final CarService carService;

    @Override
    public Mono<Void> journey(Groups group) {
        return findCarWithAvailableSeats(group)
                .defaultIfEmpty(Car.builder().build())
                .flatMap(car -> {
                    if (car.getId() == null) {
                        saveGroup(group, true);
                        return Mono.empty();
                    } else {
                        saveGroup(group, false);
                        assignGroupToCar(group, car);
                        if (car.getEmptySeats() > 0) {
                            findGroupsForCarAvailableSeats(car.getEmptySeats());
                        }
                        return Mono.empty();
                    }
                });
    }

    public Mono<Car> locate(Integer groupId) {
        return groupService.findById(Integer.valueOf(groupId))
                .defaultIfEmpty(Groups.builder().build())
                .flatMap(groups -> {
                    if (groups.getId() == null) {
                        return Mono.error(new NotFoundException());
                    } else if (groups.getWaiting()) {
                        return Mono.error(new NoContentException());
                    } else {
                        return carService.findByGroupId(groupId);
                    }
                });
    }

    public Mono<Void> dropoff(Integer groupId) {
        return carService.findByGroupId(groupId)
                .defaultIfEmpty(Car.builder().build())
                .flatMap(car -> {
                    if (car.getId() == null) {
                        return Mono.error(new NotFoundException());
                    } else {
                        return groupService.findById(groupId)
                                .flatMap(group -> {
                                    dropOffGroupFromCar(group, car);
                                    findGroupsForCarAvailableSeats(group.getPeople());

                                    return Mono.empty();
                                });
                    }
                });
    }

    private void dropOffGroupFromCar(Groups group, Car car) {
        car.getGroupIds().remove(group.getId());
        if (car.getGroupIds().isEmpty()) {
            car.setWaiting(true);
        }
        car.setEmptySeats(car.getEmptySeats() + group.getPeople());
        groupService.delete(group).subscribe();
        carService.save(car).subscribe();
    }

    private Mono<Car> findCarWithAvailableSeats(Groups group) {
        return carService.findAll()
                .filter(car -> hasCarEnoughSeatsForNewGroup(car, group))
                .next();
    }

    private boolean hasCarEnoughSeatsForNewGroup(Car car, Groups group) {
        return car.getEmptySeats() >= group.getPeople();
    }

    private void findGroupsForCarAvailableSeats(Integer seats) {
        groupService.findAll()
                .filter(groups -> groups.getWaiting())
                .filter(groups -> groups.getPeople() <= seats)
                .next()
                .subscribe(group -> journey(group).subscribe());
    }

    private void assignGroupToCar(Groups group, Car car) {
        if (car.getGroupIds() == null) {
            car.setGroupIds(new ArrayList<>());
        }
        car.getGroupIds().add(group.getId());
        car.setWaiting(false);
        car.setEmptySeats(car.getEmptySeats() - group.getPeople());
        carService.save(car).subscribe();
    }

    private void saveGroup(Groups group, boolean waiting) {
        group.setWaiting(waiting);
        groupService.save(group).subscribe();
    }
}
