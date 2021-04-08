package com.example.smartcart.ui.stats;

public class StatsItem {
    private String name;
    private Integer count;

    public StatsItem(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() { return this.name; }
    public Integer getCount() { return this.count; }
}
