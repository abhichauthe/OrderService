package com.dailycodebuffer.OrderService.external.client;

import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE", path = "/product")  // ✅ CORRECT
public interface ProductService {

    @PutMapping("/reduceQuantity/{id}")
    ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity);

    default ResponseEntity<Long> fallback(
            PaymentRequest paymentRequest,
            Exception e) {

        throw new CustomException(
                "Product Service is not available",
                "UNAVAILABLE",
                500
        );
    }

}