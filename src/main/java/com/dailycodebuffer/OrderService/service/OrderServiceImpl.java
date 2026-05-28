package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.repository.OrderRepsitory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepsitory orderRepsitory;

    @Autowired
    private ProductService productService;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        //Order Entity -> Save the data with Status Order Created
        //Product Service - Block products(Reduce the quntity)
        //Payment Service ->Payment -> Sucess -> COMPLETE -> OrElse CANCELLED

        log.info("Placing Order Request :{}" , orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(),orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount() )
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .OrderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepsitory.save(order);
        log.info("Order Places sucessfully with  Order Id : {}",order.getId());
        return order.getId();
    }
}
