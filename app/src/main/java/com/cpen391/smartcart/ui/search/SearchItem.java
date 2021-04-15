package com.cpen391.smartcart.ui.search;

import java.math.BigDecimal;

/**
 * Represents a searchable item from the database
 */
public class SearchItem {
    private String itemName;
    private BigDecimal price;
    private String barcode;
    final public boolean requiresWeighing;

    public SearchItem(String itemName, Double price, String barcode, boolean requiresWeighing) {
        this.itemName = itemName;
        this.price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = barcode;
        this.requiresWeighing = requiresWeighing;
    }

    public SearchItem(String itemName, BigDecimal price, String barcode, boolean requiresWeighing) {
        this.itemName = itemName;
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = barcode;
        this.requiresWeighing = requiresWeighing;
    }

    public String getName() {
        return this.itemName;
    }

    public BigDecimal getPrice() { return this.price; }

    public String getBarcode() { return this.barcode; }
}
