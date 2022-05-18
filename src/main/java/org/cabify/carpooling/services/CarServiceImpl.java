package org.cabify.carpooling.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.repositories.CarRepository;
import org.cabify.carpooling.repositories.GroupRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final GroupRepository groupRepository;

    @Override
    public Mono<Void> cars(List<Car> cars) {
        carRepository.deleteAll().subscribe();
        groupRepository.deleteAll().subscribe();
        carRepository.saveAll(cars).subscribe();
        return Mono.empty();
    }

    @Override
    public Flux<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Mono<Car> save(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Mono<Car> findByGroupId(Integer id) {
        return carRepository.findAll()
                .filter(car -> car.getGroupIds().contains(id))
                .next();
    }

    @Override
    public Mono<Car> findById(Integer id) {
        return carRepository.findAll()
                .filter(car -> car.getId().equals(id))
                .next();
    }

    @Override
    public Mono<Void> deleteAll() {
        return carRepository.deleteAll();
    }
}