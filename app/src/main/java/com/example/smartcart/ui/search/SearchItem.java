package com.example.smartcart.ui.search;

import java.math.BigDecimal;

public class SearchItem {
    private String itemName;
    private BigDecimal price;
    private String barcode;

    public SearchItem(String itemName, Double price, String barcode) {
        this.itemName = itemName;
        this.price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = barcode;
    }

    public SearchItem(String itemName, BigDecimal price, String barcode) {
        this.itemName = itemName;
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = barcode;
    }

    public String getName() {
        return this.itemName;
    }

    public BigDecimal getPrice() { return this.price; }

    public String getBarcode() { return this.barcode; }
}
