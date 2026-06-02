package com.dailycodebuffer.OrderService.external.request;

import com.dailycodebuffer.OrderService.model.paymentMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    private long orderId;
    private long amount;
    private String referenceNumber;
    private paymentMode paymentMode;


}
