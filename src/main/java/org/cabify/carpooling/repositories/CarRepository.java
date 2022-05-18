package org.cabify.carpooling.repositories;

import org.cabify.carpooling.domain.Car;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends ReactiveCrudRepository<Car, Integer> {
}
