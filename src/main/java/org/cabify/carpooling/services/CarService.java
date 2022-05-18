package org.cabify.carpooling.services;

import org.cabify.carpooling.domain.Car;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CarService {

    Mono<Void> cars(List<Car> cars);

    Flux<Car> findAll();

    Mono<Car> save(Car car);

    Mono<Car> findByGroupId(Integer id);

    Mono<Car> findById(Integer id);

    Mono<Void> deleteAll();

}
