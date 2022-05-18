package org.cabify.carpooling.services;

import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.repositories.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GroupServiceImplTest {

    @Mock
    GroupRepository groupRepository;

    @Spy
    @InjectMocks
    GroupServiceImpl groupService;


    @Test
    void given_listOfGroups_when_findOne_then_groupIsReturned() {
        List<Groups> groups = new ArrayList<>();
        groups.add(Groups.builder().id(1).people(2).build());
        groups.add(Groups.builder().id(2).people(3).build());

        doReturn(Flux.fromIterable(groups)).when(groupRepository).findAll();

        groupService.findById(2)
                .as(StepVerifier::create)
                .expectNextMatches(group -> group.getId().equals(2))
                .verifyComplete();

        groupService.findById(1234)
                .as(StepVerifier::create)
                .verifyComplete();
    }
}