package com.infnet.order_service.client;

import com.infnet.catalog_service.model.Product;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class CatalogClient {
    private final WebClient wc;

    public CatalogClient(WebClient.Builder builder) {
        this.wc = builder.baseUrl("http://localhost:8081").build();
    }

    public Mono<Product> getProduct(Long id) {
        return wc.get().uri("/products/{id}", id)
                .retrieve()
                .bodyToMono(Product.class);
    }
}
