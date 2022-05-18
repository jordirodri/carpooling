package org.cabify.carpooling.repositories;

import org.cabify.carpooling.domain.Groups;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends ReactiveCrudRepository<Groups, Integer> {
}
