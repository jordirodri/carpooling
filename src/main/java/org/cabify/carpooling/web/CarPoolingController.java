package org.cabify.carpooling.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.domain.Identifier;
import org.cabify.carpooling.services.CarService;
import org.cabify.carpooling.services.GroupService;
import org.cabify.carpooling.services.JourneyService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class CarPoolingController {

    private final CarService carService;

    private final GroupService groupService;

    private final JourneyService journeyService;

    @GetMapping("/status")
    public ResponseEntity<Void> status() {
        return ResponseEntity.ok().build();
    }

    @PutMapping("/cars")
    public ResponseEntity<Mono<Void>> cars(@RequestBody List<Car> cars) {
        return ResponseEntity.ok(carService.cars(cars));
    }

    @PostMapping("/journey")
    public ResponseEntity<Mono<Void>> journey(@RequestBody Groups group) {
        return ResponseEntity.ok(journeyService.journey(group));
    }

    @PostMapping(value = "/dropoff", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Mono<Void>> dropoff(Identifier groupId) {
        if (groupId == null || groupId.getID() == null) {
            return ResponseEntity.badRequest().build();
        } else {
            try {
                return ResponseEntity.ok(journeyService.dropoff(Integer.valueOf(groupId.getID())));
            } catch (NumberFormatException e) {
                log.warn("GroupId is not a number: {}", groupId);
                return ResponseEntity.badRequest().build();
            }
        }
    }

    @PostMapping(value = "/locate", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ResponseEntity<Mono<Car>> locate(Identifier groupId) {
        if (groupId == null || groupId.getID() == null) {
            return ResponseEntity.badRequest().build();
        } else {
            try {
                return ResponseEntity.ok(journeyService.locate(Integer.valueOf(groupId.getID())));
            } catch (NumberFormatException e) {
                log.warn("GroupId is not a number: {}", groupId);
                return ResponseEntity.badRequest().build();
            }
        }
    }

    //Added endpoints for testing in Swagger
    @GetMapping("/findAllCars")
    public ResponseEntity<Flux<Car>> findAllCars() {
        return ResponseEntity.ok(carService.findAll());
    }

    @GetMapping("/findAllGroups")
    public ResponseEntity<Flux<Groups>> findAllGroups() {
        return ResponseEntity.ok(groupService.findAll());
    }

    @ExceptionHandler
    ResponseEntity<Void> handleNotFound(NotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler
    ResponseEntity<Void> handleNoContent(NoContentException e) {
        return ResponseEntity.noContent().build();
    }

}
