package com.example.smartcart.ui.shopping;

import java.math.BigDecimal;

/**
 * Represents an item in the shoppingList
 *
 */
public class ShoppingListItem {
    private Integer quantity;
    private final String itemName;
    private final BigDecimal price;
    private BigDecimal totalPrice;

    public ShoppingListItem(int quantity, String itemName, double price) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.totalPrice = this.price.multiply(new BigDecimal(quantity));
        this.totalPrice = this.totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public ShoppingListItem(int quantity, String itemName, BigDecimal price) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.totalPrice = this.price.multiply(new BigDecimal(quantity));
        this.totalPrice = this.totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.totalPrice = this.price.multiply(new BigDecimal(quantity));
        this.totalPrice = this.totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String getItemName() {
        return itemName;
    }
    public BigDecimal getPrice() {return price; }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}