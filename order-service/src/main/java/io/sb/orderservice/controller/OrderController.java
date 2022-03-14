package io.sb.orderservice.controller;

import io.sb.orderservice.exception.OrderFailedException;
import io.sb.orderservice.model.OrderResponseVO;
import io.sb.orderservice.model.OrderVO;
import io.sb.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    @Autowired
    OrderService service;
    public OrderController(OrderService service){
        this.service = service;
    }

    @PostMapping("/v1/order")
    public ResponseEntity<OrderResponseVO> createOrder(@RequestBody OrderVO orderVO) throws OrderFailedException {
        return ResponseEntity.ok(service.createOrder(orderVO));
    }
}
