package com.example.smartcart.ui.shopping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDate;

public class ShoppingList {
    private ArrayList<ShoppingListItem> items;
    private BigDecimal totalPrice;
    private LocalDate purchaseDate;
    private String name;

    public ShoppingList(ArrayList<ShoppingListItem> shoppingListItems, BigDecimal totalPrice, String name) {
        this.items = new ArrayList<>(shoppingListItems);
        this.totalPrice = new BigDecimal(String.valueOf(totalPrice));
        this.purchaseDate = LocalDate.now();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getTotalPrice() {
        return totalPrice.toString();
    }

    public String getPurchaseDate() {
        return purchaseDate.toString();
    }
}
