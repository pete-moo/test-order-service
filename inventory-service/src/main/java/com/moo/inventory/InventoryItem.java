package com.moo.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class InventoryItem {

    @Id
    private String productId;
    private String name;
    private int quantity;

    protected InventoryItem() {}

    public InventoryItem(String productId, String name, int quantity) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }

    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}