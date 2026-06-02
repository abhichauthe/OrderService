package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import com.dailycodebuffer.OrderService.external.request.PaymentResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.model.ProductResponse;
import com.dailycodebuffer.OrderService.repository.OrderRepsitory;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepsitory orderRepsitory;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        log.info("Placing Order Request: {}", orderRequest);

        // Step 1 - Reduce product quantity
        ResponseEntity<Void> response = productService.reduceQuantity(
                orderRequest.getProductId(), orderRequest.getQuantity());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Product quantity reduction failed");
        }

        // Step 2 - Save order with CREATED status
        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .OrderDate(Instant.now())        // ✅ camelCase
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepsitory.save(order);

        // Step 3 - Call payment service
        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();

        String orderStatus;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing Order Status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Payment failed. Changing Order Status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        // Step 4 - Update final order status
        order.setOrderStatus(orderStatus);
        orderRepsitory.save(order);

        log.info("Order placed successfully with Order Id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order Details for Order Id :{} " ,orderId );
        Order order
                 = orderRepsitory.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found for the order Id: " + orderId,"Not found", 404));

                log.info("Invoking Product Service to fetch the product for id :", order.getProductId());

        ProductResponse productResponse = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class);

        log.info("Getting payment Information from the payment Service");

        PaymentResponse paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(), PaymentResponse.class);

        OrderResponse.ProductDetails productDetails
                =OrderResponse.ProductDetails.builder()
                .productName(productResponse.getProductName())
                .productId(productResponse.getProductId())
                .build();

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentStatus(paymentResponse.getStatus())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderstatus(order.getOrderStatus())
                .amount(order.getAmount())
                .orderDate(order.getOrderDate())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

        return orderResponse ;
    }
}
