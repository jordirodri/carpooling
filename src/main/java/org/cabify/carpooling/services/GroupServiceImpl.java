package org.cabify.carpooling.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.repositories.GroupRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    public Mono<Groups> findById(Integer id) {
        return groupRepository.findAll()
                .filter(groups -> groups.getId().equals(id))
                .next();
    }

    @Override
    public Flux<Groups> findAll() {
        return groupRepository.findAll();
    }

    @Override
    public Mono<Void> delete(Groups group) {
        return groupRepository.delete(group);
    }

    @Override
    public Mono<Void> deleteAll() {
        return groupRepository.deleteAll();
    }

    @Override
    public Mono<Groups> save(Groups group) {
        return groupRepository.save(group);
    }

}
