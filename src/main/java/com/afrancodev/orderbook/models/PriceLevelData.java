package com.afrancodev.orderbook.models;

import java.util.List;

public class PriceLevelData {
    public final List<Double> prices;
    public final List<Integer> quantities;

    public PriceLevelData(List<Double> prices, List<Integer> quantities) {
        this.prices = prices;
        this.quantities = quantities;
    }
}
