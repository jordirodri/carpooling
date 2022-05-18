package org.cabify.carpooling.services;

import org.cabify.carpooling.domain.Groups;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupService {


    Mono<Groups> findById(Integer id);

    Flux<Groups> findAll();

    Mono<Void> delete(Groups group);

    Mono<Void> deleteAll();

    Mono<Groups> save(Groups group);

}
