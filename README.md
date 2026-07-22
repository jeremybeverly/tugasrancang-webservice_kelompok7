# 🛠️ Panduan Langkah-Langkah Git (Wajib Diikuti!)

Bagi teman-teman yang belum terbiasa dengan Git, ikuti urutan perintah ini setiap kali kalian ingin mengerjakan tugas masing-masing.

---

### Langkah 1: Ambil Project Terbaru & Pindah ke Main
Sebelum mulai mengetik kode apa pun, pastikan posisi kalian berada di branch `main` utama dan kodenya adalah yang paling update dari GitHub.
```bash
git checkout main
git pull origin main
```

### JANGAN LANGSUNG CODING DI MAIN! Buat branch kalian sendiri terlebih dahulu lewat command-command ini:

* Anggota 2: git checkout -b feature/route-one
* Anggota 3: git checkout -b feature/route-two
* Anggota 4: git checkout -b feature/jwt-filter
* Anggota 5: git checkout -b feature/bff-dashboard
* Anggota 6: git checkout -b feature/rate-limiting

(Setelah mengetik perintah di atas, barulah kalian silakan buka vscode atau tools lain baru mulai coding).

### Langkah 3: Simpan dan Upload Tugas ke GitHub
Jika tugas sudah selesai dibuat dan aplikasi sudah dites, jalankan 3 baris perintah ini secara berurutan di terminal untuk meng-upload kode kalian:
```bash
git add .
git commit -m "feat: menyelesaikan tugas anggota X"
git push origin HEAD
```


# 🚀 API Gateway & BFF - Kelompok 7 (Topik 4)

Repositori ini berisi implementasi proyek **API Gateway** dan **BFF (Backend for Frontend)** menggunakan Spring Boot / Spring Cloud Gateway. Proyek ini dirancang secara kolaboratif oleh 6 anggota kelompok dengan pembagian tugas yang terstruktur.

---

## 👥 Pembagian Tugas & Struktur Anggota

| No. Anggota | Tugas Utama | Komponen / File Terkait |
| :--- | :--- | :--- |
| **Anggota 1** | Mock Endpoint & Routing Base | `GatewayApplication.java`, `MockServiceController.java`, & pengaturan dasar `application.properties` |
| **Anggota 2** | API Gateway Route 1 | Konfigurasi `route_service1` di `application.properties` |
| **Anggota 3** | API Gateway Route 2 | Konfigurasi `route_service2` (dengan batasan *Method=GET*) di `application.properties` |
| **Anggota 4** | JWT Global Filter | `JwtGlobalFilter.java` & konfigurasi `jwt.secret` |
| **Anggota 5** | BFF (Backend for Frontend) | `BffController.java` & `WebClientConfig.java` |
| **Anggota 6** | Rate Limiting & Dokumen | `RateLimiter.java`, dependensi Bucket4j di `pom.xml`, dan berkas `README.md` |

---

## 📂 Struktur Berkas Kode Proyek

Berikut adalah implementasi kode lengkap yang digunakan dalam proyek ini:

### 1. Konfigurasi Sistem (`application.properties`)
```properties
server.port=8080
spring.cloud.gateway.enabled=true

# --- Konfigurasi Route 1 ---
spring.cloud.gateway.routes[0].id=route_service1
spring.cloud.gateway.routes[0].uri=http://localhost:8080
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/service1/**
spring.cloud.gateway.routes[0].filters[0]=RewritePath=/api/service1/(?<segment>.*), /service1/${segment}


# --- Konfigurasi Route 2 (Tugas Anggota 3) ---
spring.cloud.gateway.routes[1].id=route_service2
spring.cloud.gateway.routes[1].uri=http://localhost:8082 
spring.cloud.gateway.routes[1].predicates[0]=Path=/api/service2/**
# Ini wajib ada sesuai instruksi
spring.cloud.gateway.routes[1].predicates[1]=Method=GET 
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/api/service2/(?<segment>.*), /service2/${segment}


# testing
jwt.secret=testing_token_rahasia_untuk_tugas_anggota_empat_kelompok_tujuh

```

### 2. Main Application (`GatewayApplication.java`)

```java
package com.kelompok7.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

```

### 3. Mock Service Controller (`MockServiceController.java`)

```java
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

```

### 4. BFF Controller & WebClient Config (`BffController.java` & `WebClientConfig.java`)

```java
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

```

```java
package com.kelompok7.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}

```

### 5. Keamanan JWT Filter (`JwtGlobalFilter.java`)

```java
package com.kelompok7.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtGlobalFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }
        filterChain.doFilter(request, response);
    }
}

```

### 6. Rate Limiting (`RateLimiter.java`)

```java
package com.kelompok7.gateway;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(1)
public class RateLimiter implements Filter {

    @SuppressWarnings("deprecation")
    private final Bucket bucket = Bucket.builder()
            .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
            .build();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429); 
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Maksimal 10 request per menit.\"}");
            httpResponse.getWriter().flush();
        }
    }
}

```

---

## ⚙️ Cara Menjalankan Proyek

1. **Clone repository ini ke komputer lokal:**
```bash
git clone <url-repository-kalian>

```


2. **Buka folder proyek** melalui IDE pilihanmu (seperti IntelliJ IDEA atau VS Code).
3. **Jalankan aplikasi** menggunakan Maven:
```bash
mvn spring-boot:run

```


4. Pastikan aplikasi berjalan sukses di port `8080`.

---

## 📸 Dokumentasi Pengujian Global (Testing)

* **Uji Coba Route 1 (Profile):** `GET http://localhost:8080/api/service1/profile`
* <img width="1085" height="1037" alt="image" src="https://github.com/user-attachments/assets/e86a158b-b7e6-4dea-9aaa-b01ea8d49e30" />

* **Uji Coba Route 2 (Inventory):** `GET http://localhost:8080/api/service2/inventory` (Khusus metode `GET`)
* <img width="1108" height="1052" alt="image" src="https://github.com/user-attachments/assets/d88ea1f0-6628-44ff-921b-1ed3743817a6" />

* **Uji Coba BFF Dashboard:** `GET http://localhost:8080/api/bff/dashboard`
* <img width="1600" height="900" alt="image" src="https://github.com/user-attachments/assets/d15b0f37-573f-4c7b-a87f-4dc9bc83ea9b" />

* **Uji Coba Rate Limiting:** Akses berlebih melebihi 10 kali dalam 1 menit akan menghasilkan respons HTTP `429 Too Many Requests`.
* <img width="1600" height="900" alt="image" src="https://github.com/user-attachments/assets/df17787b-5524-453d-9386-0231592b4e66" />


```

```
