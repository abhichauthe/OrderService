package com.dailycodebuffer.OrderService.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/product")
public interface ProductService {

    @PutMapping("/reduceQuantity/{id}")                  // ✅ mapping on method
   ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,              // ✅ @PathVariable on parameter
            @RequestParam long quantity);
}
