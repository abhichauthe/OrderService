package com.dailycodebuffer.OrderService.external.client;

import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE", path = "/payment")
public interface PaymentService {

    @PostMapping
    @CircuitBreaker(name = "external", fallbackMethod = "fallback")
    ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    default ResponseEntity<Long> fallback(
            PaymentRequest paymentRequest,
            Exception e) {

        throw new CustomException(
                "Payment Service is not available",
                "UNAVAILABLE",
                500
        );
    }
}