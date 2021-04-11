package com.example.smartcart.ui.shopping;

import java.math.BigDecimal;
import com.example.smartcart.ui.search.SearchItem;

/**
 * Represents an item in the shoppingList
 *
 */
public class ShoppingListItem {
    private Integer quantity;
    private String barcode;
    private final String itemName;
    private final BigDecimal price;
    private BigDecimal totalPrice;
    private BigDecimal weight;

    public ShoppingListItem(int quantity, String itemName, double price, double weight) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.totalPrice = this.price.multiply(new BigDecimal(quantity));
        this.totalPrice = this.totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.weight = new BigDecimal("0.0");
    }

    public ShoppingListItem(int quantity, SearchItem searchitem) {
        this.quantity = quantity;
        this.itemName = searchitem.getName();
        this.price = searchitem.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = searchitem.getBarcode();
        this.totalPrice = this.price.multiply(new BigDecimal(quantity));
        this.totalPrice = this.totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.weight = new BigDecimal("0.0");
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.totalPrice = this.price.multiply(new BigDecimal(quantity));
        this.totalPrice = this.totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    public String getBarcode() {return barcode; }
    public String getItemName() {
        return itemName;
    }
    public BigDecimal getPrice() {return price; }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public String getWeightString() {
        if (weight.compareTo(BigDecimal.ZERO) == 0) {
            return "";
        }
        return weight.toString();
    }
    public BigDecimal getWeight() {
        return weight;
    }
}