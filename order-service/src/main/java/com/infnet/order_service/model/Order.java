package com.infnet.order_service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Table("orders")
public class Order {
    @Id
    private Long Id;
    private Long productId;
    private int qty;
    private BigDecimal total;
    private Instant createdAt;

    public Order(Long id, Long productId, int qty, BigDecimal total, Instant createdAt) {
        this.Id = id;
        this.productId = productId;
        this.qty = qty;
        this.total = total;
        this.createdAt = createdAt;
    }

    public Long getId() { return Id; }
    public void setId(Long id) { this.Id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(Id, order.Id);
    }

    @Override
    public int hashCode() { return Objects.hash(Id); }
}
