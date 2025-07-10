package com.afrancodev.orderbook.models;

public class Trade {
    private final boolean isBuy;
    private final double price;
    private final int quantity;

    public Trade(boolean isBuy, double price, int quantity) {
        this.isBuy = isBuy;
        this.price = price;
        this.quantity = quantity;
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

    @Override
    public String toString() {
        return String.format("Trade[%s %d @ %.2f]", isBuy ? "BUY" : "SELL", quantity, price);
    }
}
