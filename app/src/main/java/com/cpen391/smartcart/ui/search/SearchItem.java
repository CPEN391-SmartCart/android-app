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

    /**
     * Makes a new search item with the given arguments
     * @param itemName
     * @param price
     * @param barcode
     * @param requiresWeighing
     */
    public SearchItem(String itemName, Double price, String barcode, boolean requiresWeighing) {
        this.itemName = itemName;
        this.price = new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = barcode;
        this.requiresWeighing = requiresWeighing;
    }

    /**
     * Makes a new search item with the given arguments
     * @param itemName
     * @param price
     * @param barcode
     * @param requiresWeighing
     */
    public SearchItem(String itemName, BigDecimal price, String barcode, boolean requiresWeighing) {
        this.itemName = itemName;
        this.price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.barcode = barcode;
        this.requiresWeighing = requiresWeighing;
    }

    /**
     * Gets the name
     * @return the name of the item
     */
    public String getName() {
        return this.itemName;
    }

    /**
     * Gets the price of the item
     * @return the price
     */
    public BigDecimal getPrice() { return this.price; }

    /**
     * Gets the barcode
     * @return the barcode
     */
    public String getBarcode() { return this.barcode; }
}
