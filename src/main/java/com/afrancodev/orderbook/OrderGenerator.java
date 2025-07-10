package com.afrancodev.orderbook;

import java.util.concurrent.ThreadLocalRandom;

import com.afrancodev.orderbook.models.Order;

public class OrderGenerator {
    private static final double MARKET_ORDER_PROBABILITY = 0.15;
    private static final double LARGE_ORDER_PROBABILITY = 0.05;
    private static final double SPREAD_WIDTH = 10.0;
    private static final int SMALL_ORDER_MIN = 1;
    private static final int SMALL_ORDER_MAX = 100;
    private static final int LARGE_ORDER_MIN = 500;
    private static final int LARGE_ORDER_MAX = 1000;
    private static final int MIN_AGE = 10;
    private static final int MAX_AGE = 100;

    private double fairPrice;

    public OrderGenerator(double initialFairPrice) {
        this.fairPrice = initialFairPrice;
    }

    public double getFairPrice() {
        return fairPrice;
    }

    public void updateFairPrice() {
        fairPrice += ThreadLocalRandom.current().nextGaussian() * 0.5;
    }

    public Order generateRandomOrder(OrderBook orderBook) {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        boolean isBuy = rand.nextBoolean();
        boolean isMarketOrder = rand.nextDouble() <= MARKET_ORDER_PROBABILITY;

        double limitPrice;
        if (isMarketOrder) {
            Double bestBid = orderBook.getCurrentBid();
            Double bestAsk = orderBook.getCurrentAsk();
            limitPrice = isBuy
                    ? (bestAsk != null ? bestAsk : fairPrice)
                    : (bestBid != null ? bestBid : fairPrice);
        } else {
            double priceOffset = rand.nextDouble(0, SPREAD_WIDTH);
            limitPrice = isBuy ? fairPrice - priceOffset : fairPrice + priceOffset;
        }

        boolean isLargeOrder = rand.nextDouble() < LARGE_ORDER_PROBABILITY;
        int quantity = isLargeOrder
                ? rand.nextInt(LARGE_ORDER_MIN, LARGE_ORDER_MAX)
                : rand.nextInt(SMALL_ORDER_MIN, SMALL_ORDER_MAX);

        int age = rand.nextInt(MIN_AGE, MAX_AGE);

        return new Order(isBuy, round(limitPrice), quantity, isMarketOrder, age);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
