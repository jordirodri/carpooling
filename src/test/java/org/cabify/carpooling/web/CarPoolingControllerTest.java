package org.cabify.carpooling.web;

import org.cabify.carpooling.domain.Car;
import org.cabify.carpooling.domain.Groups;
import org.cabify.carpooling.services.CarService;
import org.cabify.carpooling.services.GroupService;
import org.cabify.carpooling.services.JourneyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
class CarPoolingControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    CarService carService;

    @MockBean
    GroupService groupService;

    @MockBean
    JourneyService journeyService;

    @Test
    void given_serviceHasStartedUp_when_statusIsCalled_then_responseIsHttp200() {

        webTestClient.get()
                .uri("/status")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void given_carsData_when_carsIsCalled_then_responseIsHttp200() {

        List<Car> cars = IntStream.range(0, 10)
                .boxed()
                .map(num -> Car.builder().id(num).seats(num % 6).build())
                .collect(Collectors.toList());

        webTestClient.put()
                .uri("/cars")
                .body(BodyInserters.fromValue(cars))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void given_wrongRequestData_when_carsIsCalled_then_responseIsHttp400badRequest() {

        List<String> wrongData = IntStream.range(0, 100)
                .boxed()
                .map(num -> num.toString())
                .collect(Collectors.toList());

        webTestClient.put()
                .uri("/cars")
                .body(BodyInserters.fromValue(wrongData))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void given_groupOfPeople_when_journeyIsCalled_then_responseIsHttp200() {
        Groups group = Groups.builder().id(1).people(4).build();

        webTestClient.post()
                .uri("/journey")
                .body(BodyInserters.fromValue(group))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void given_wrongRequestData_when_journeyIsCalled_then_responseIsHttp400BadRequest() {
        String wrongTypeOfData = "wrongTypeOfData";

        webTestClient.post()
                .uri("/journey")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(wrongTypeOfData))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void given_groupOfPeople_when_dropOffIsCalled_then_responseIsHttp200() {

        webTestClient.post()
                .uri("/dropoff")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("ID", "1"))
                .exchange()
                .expectStatus().isOk();
    }

    //@Test
    void given_wrongRequestData_when_dropOffIsCalled_then_responseIsHttp400BadRequest() {
        String wrongData = "wongDataForRequest";

        webTestClient.post()
                .uri("/dropoff")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(wrongData))
                .exchange()
                .expectStatus().isBadRequest();
    }

//    @Test
    void given_notExistingGroup_when_dropOffIsCalled_then_responseIsHttp404NotFound() {

        webTestClient.post()
                .uri("/dropoff")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("ID", "1234"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void given_groupOfPeople_when_locateIsCalled_then_responseIsHttp200() {

        when(journeyService.locate(any())).thenReturn(Mono.just(Car.builder().id(2).seats(2).build()));

        webTestClient.post()
                .uri("/locate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("ID", "1"))
                .exchange()
                .expectStatus().isOk();
    }

    //@Test
    void given_wrongRequestData_when_locateIsCalled_then_responseIsHttp400BadRequest() {
        Car wrongData = Car.builder().build();

        MultiValueMap multiValueMap = new LinkedMultiValueMap();

        when(groupService.findById(any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/locate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(multiValueMap))
                .exchange()
                .expectStatus().isBadRequest();
    }

    //@Test
    void given_groupNotExistingGroup_when_locateIsCalled_then_responseIsHttp404NotFound() {

//        when(groupService.findById(any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/locate")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("ID", "1234"))
                .exchange()
                .expectStatus().isNotFound();
    }


}