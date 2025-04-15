package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.OrderDTO;
import com.ecommerce.project.payload.OrderRequestDTO;
import com.ecommerce.project.payload.StripePaymentDto;
import com.ecommerce.project.service.OrderService;
import com.ecommerce.project.service.StripeService;

import com.ecommerce.project.utils.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.BASE_URL)
public class OrderController {
    private final OrderService orderService;
    private final AuthUtil authUtil;
    private final StripeService stripeService;
    
    public OrderController(
        OrderService orderService,
        AuthUtil authUtil,
        StripeService stripeService
    ) {
        this.orderService = orderService;
        this.authUtil = authUtil;
        this.stripeService = stripeService;
    }
    
    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(
        @PathVariable String paymentMethod,
        @RequestBody OrderRequestDTO orderRequestDTO
    ) {
        String emailId = authUtil.loggedInEmail();
        System.out.println("orderRequestDTO DATA: " + orderRequestDTO);
        OrderDTO order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDto stripePaymentDto) throws StripeException {
            PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);
            return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
        }
    }
