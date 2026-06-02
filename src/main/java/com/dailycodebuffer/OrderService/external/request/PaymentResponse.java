package com.dailycodebuffer.OrderService.external.request;

import com.dailycodebuffer.OrderService.model.paymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private long paymentId;
    private String status;
    private paymentMode paymentMode;
    private long amount;
    private Instant paymentDate;
    private long orderId;
}
