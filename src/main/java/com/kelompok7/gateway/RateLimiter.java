package com.kelompok7.gateway;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
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
public class GlobalRateLimiterFilter implements Filter {

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
            httpResponse.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"Maksimal 10 request per menit.\"}");
        }
    }
}