package com.kelompok7.gateway;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class BffController {

    private final WebClient webClient;

    public BffController(WebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("/api/bff/dashboard")
    public Mono<Map<String, Object>> getDashboard(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        WebClient.RequestHeadersSpec<?> profileSpec = webClient.get()
                .uri("http://localhost:8080/api/service1/profile");
        WebClient.RequestHeadersSpec<?> inventorySpec = webClient.get()
                .uri("http://localhost:8080/api/service2/inventory");

        if (authHeader != null) {
            profileSpec = profileSpec.header(HttpHeaders.AUTHORIZATION, authHeader);
            inventorySpec = inventorySpec.header(HttpHeaders.AUTHORIZATION, authHeader);
        }

        Mono<Map> profile = profileSpec.retrieve().bodyToMono(Map.class);
        Mono<Object> inventory = inventorySpec.retrieve().bodyToMono(Object.class);

        return Mono.zip(profile, inventory)
                .map(tuple -> Map.of(
                        "profile", tuple.getT1(),
                        "inventory", tuple.getT2()
                ));
    }
}