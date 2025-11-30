package com.infnet.order_service;

import com.infnet.order_service.dto.OrderRequest;
import com.infnet.order_service.model.ProductDTO;
import com.infnet.order_service.repository.OrderRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class OrderServiceIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("orderdb")
            .withUsername("sa")
            .withPassword("sa");

    static MockWebServer mockCatalog;

    @Autowired
    WebTestClient webClient;

    @Autowired
    OrderRepository repo;

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        String r2dbcUrl = String.format("r2dbc:postgresql://%s:%d/%s",
                postgres.getHost(), postgres.getFirstMappedPort(), postgres.getDatabaseName());
        registry.add("spring.r2dbc.url", () -> r2dbcUrl);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @BeforeAll
    static void setup() throws Exception {
        mockCatalog = new MockWebServer();
        mockCatalog.start();
        System.setProperty("catalog.base-url", mockCatalog.url("/").toString());
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockCatalog.shutdown();
    }

    @Test
    void createOrder_happyPath() throws Exception {
        ProductDTO product = new ProductDTO(1L, "Caneta", new BigDecimal("2.50"));
        String body = String.format("{\"id\":%d,\"name\":\"%s\",\"price\":%s}",
                product.getId(), product.getName(), product.getPrice().toString());

        mockCatalog.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        OrderRequest req = new OrderRequest(1L, 3);

        webClient.post()
                .uri("/orders")
                .body(Mono.just(req), OrderRequest.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(1)
                .jsonPath("$.qty").isEqualTo(3)
                .jsonPath("$.total").isEqualTo(7.5);
    }
}
