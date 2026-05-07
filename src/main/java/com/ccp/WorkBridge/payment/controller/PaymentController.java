package com.ccp.WorkBridge.payment.controller;

import com.ccp.WorkBridge.payment.dto.CapturePaymentRequest;
import com.ccp.WorkBridge.payment.dto.PaymentIntentResult;
import com.ccp.WorkBridge.payment.service.OrderPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderPaymentService orderPaymentService;

    @PostMapping("/process")
    @ResponseStatus(HttpStatus.OK)
    public PaymentIntentResult processPayment(@RequestBody Long paymentId){
        return orderPaymentService.processPayment(paymentId);
    }

}
