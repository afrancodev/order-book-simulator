package com.afrancodev.orderbook.models;

public class Order {

    private final boolean isBuy;
    private final double price;
    private final boolean marketOrder;
    private int quantity;
    private int age;

    public Order(boolean isBuy, double price, int quantity, boolean isMarketOrder, int age) {
        this.isBuy = isBuy;
        this.price = price;
        this.quantity = quantity;
        this.marketOrder = isMarketOrder;
        this.age = age;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void reduceQuantity(int amount) {
        this.quantity -= amount;
    }

    public boolean isMarketOrder(){
        return this.marketOrder;
    }

    public void decreaseAge() {
        age--;
    }

    public boolean isExpired() {
        return age <= 0;
    }

    @Override
    public String toString() {
        return (marketOrder ? "MARKET " : "LIMIT ") + (isBuy ? "BUY" : "SELL") + " " + quantity + " @ " + price;
    }
}
