package com.kelompok7.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@RestController
public class MockServiceController {
    @GetMapping("/api/service1/profile")
    public Mono<Map<String, String>> getProfile() {
        return Mono.just(Map.of(
                "status", "Success",
                "service", "Service 1 (Profile)",
                "username", "admin"
        ));
    }

    @GetMapping("/api/service2/inventory")
    public Mono<List<String>> getInventory() {
        return Mono.just(List.of(
                "Item 001: Cardboard Box (50 Units)",
                "Item 002: Plastic Wrap (12 Units)",
                "Item 003: Wooden Pallet (8 Units)"
        ));
    }
}
