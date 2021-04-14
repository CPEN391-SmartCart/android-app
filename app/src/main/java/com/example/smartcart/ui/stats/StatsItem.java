package com.example.smartcart.ui.stats;

import java.math.BigDecimal;

/**
 * Represents a stats item to display in the statsFragment
 */
public class StatsItem {
    private String name;
    private Integer count;
    private BigDecimal cost;

    public StatsItem(String name, Integer count, Double cost) {
        this.name = name;
        this.count = count;
        this.cost = new BigDecimal(cost).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public String getName() { return this.name; }
    public Integer getCount() { return this.count; }
    public BigDecimal getCost() { return this.cost; }
}
