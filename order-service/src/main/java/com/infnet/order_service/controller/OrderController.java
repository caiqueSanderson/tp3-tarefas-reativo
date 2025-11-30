package com.infnet.order_service.controller;

import com.infnet.order_service.client.CatalogClient;
import com.infnet.order_service.dto.OrderRequest;
import com.infnet.order_service.repository.OrderRepository;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
public class OrderController {
    private final CatalogClient client;
    private final OrderRepository repo;


    public OrderController(CatalogClient client, OrderRepository repo) {
        this.client = client;
        this.repo = repo;
    }

    @PostMapping("/orders")
    public Mono<ResponseEntity<Order>> createOrder(@RequestBody OrderRequest request) {
        return client.getProduct(request.getProductId())
                .flatMap(product -> {
                    Order o = new Order();
                    o.setProductId(product.getId());
                    o.setQty(request.getQty());
                    BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(request.getQty()));
                    o.setTotal(total);
                    o.setCreatedAt(Instant.now());
                    return orderRepository.save(o);
                })
                .map(saved -> ResponseEntity.status(201).body(saved))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/orders/{id}")
    public Mono<ResponseEntity<Order>> getOrder(@PathVariable Long Id) {
        return repo.findById(Id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
