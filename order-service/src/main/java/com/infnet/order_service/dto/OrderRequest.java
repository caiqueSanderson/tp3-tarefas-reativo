package com.infnet.order_service.dto;

public class OrderRequest {
    private Long productId;
    private int qty;

    public OrderRequest() {}
    public OrderRequest(Long productId, int qty) { this.productId = productId; this.qty = qty; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = qty; }
}
