package com.infnet.catalog_service.controller;

import com.infnet.catalog_service.model.Product;
import com.infnet.catalog_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Flux<Product> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> findById(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<Product> create(@RequestBody Product product) {
        return service.create(product);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> update(@PathVariable UUID id, @RequestBody Product p) {
        return service.update(id, p)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable UUID id) {
        return service.delete(id)
                .then(Mono.just(ResponseEntity.ok().build()));
    }
}
