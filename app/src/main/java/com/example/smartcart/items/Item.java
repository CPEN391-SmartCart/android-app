package com.example.smartcart.items;

public class Item
{
    private String name;
    private double price;
    private boolean byWeight;
    private double weight;

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setByWeight(boolean byWeight)
    {
        this.byWeight = byWeight;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public boolean getByWeight() {
        return this.byWeight;
    }

    public double getWeight()
    {
        return this.weight = weight;
    }
}
