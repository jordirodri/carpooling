package org.cabify.carpooling.services;

import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import reactor.core.publisher.Mono;

public interface JourneyService {

    Mono<Void> journey(Groups group);

    Mono<Car> locate(Integer groupId);

    Mono<Void> dropoff(Integer groupId);
}
