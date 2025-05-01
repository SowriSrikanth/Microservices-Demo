package com.microservices.orderservice.controller;

import com.microservices.orderservice.client.ProductClient;
import com.microservices.orderservice.dto.Product;
import com.microservices.orderservice.model.Order;
import com.microservices.orderservice.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderController(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @GetMapping("/{id}/products")
    public Flux<Product> getProductsForOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null || order.getProductIds() == null) {
            return Flux.empty();
        }
        return Flux.fromIterable(order.getProductIds())
                .flatMap(productClient::getProductById);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }
}