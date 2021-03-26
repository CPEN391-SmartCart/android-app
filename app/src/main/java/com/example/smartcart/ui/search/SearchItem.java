package com.example.smartcart.ui.search;

import java.math.BigDecimal;

public class SearchItem {
    private String itemName;
    private BigDecimal price;

    public SearchItem(String itemName, Double price) {
        this.itemName = itemName;
        this.price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String getName() {
        return this.itemName;
    }

    public String getPrice() { return "$" + this.price.toString(); }
}
