package com.cpen391.smartcart.ui.shopping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents a shopping list
 */
public class ShoppingList {
    private ArrayList<ShoppingListItem> items;
    private BigDecimal subtotal;
    private BigDecimal gst;
    private BigDecimal totalPrice;
    private String purchaseDate;
    private String name;

    public ShoppingList(ArrayList<ShoppingListItem> shoppingListItems, BigDecimal subtotal, String name) {
        this.items = new ArrayList<>(shoppingListItems);
        this.subtotal = new BigDecimal(String.valueOf(subtotal)).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.gst = this.subtotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, BigDecimal.ROUND_HALF_UP);;
        this.totalPrice = this.subtotal.add(gst);
        this.purchaseDate = LocalDate.now().toString();
        this.name = name;
    }

    public ShoppingList(ArrayList<ShoppingListItem> shoppingListItems, double subtotal, String purchaseDate) {
        this.items = new ArrayList<>(shoppingListItems);
        this.subtotal = new BigDecimal(String.valueOf(subtotal)).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.gst = this.subtotal.multiply(BigDecimal.valueOf(0.05)).setScale(2, BigDecimal.ROUND_HALF_UP);;
        this.totalPrice = this.subtotal.add(gst);
        this.purchaseDate = purchaseDate;
        this.name = "ShoppingList";
    }

    public ArrayList<ShoppingListItem> getItems() { return items; }

    public String getName() {
        return name;
    }
    public double getSubTotal() { return subtotal.doubleValue(); }
    public double getGST() { return gst.doubleValue(); }

    public String getTotalPrice() {
        return totalPrice.toString();
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }
}
