package com.kelompok7.gateway;

import org.springframework.web.bind.annotation.GetMapping;
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
public Mono<Map<String, Object>> getDashboard() {

        Mono<Map> profile = webClient.get()
            .uri("http://localhost:8080/api/service1/profile")
            .retrieve()
            .bodyToMono(Map.class);

        Mono<Object> inventory = webClient.get()
            .uri("http://localhost:8080/api/service2/inventory")
            .retrieve()
            .bodyToMono(Object.class);

        return Mono.zip(profile, inventory)
            .map(tuple -> Map.of(
                    "profile", tuple.getT1(),
                    "inventory", tuple.getT2()
            ));
    }
}