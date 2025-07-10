package com.afrancodev.orderbook.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeHistory {
    private final List<Trade> trades = new ArrayList<>();

    public synchronized void recordTrade(Trade trade) {
        trades.add(trade);
    }

    public synchronized List<Trade> getAllTrades() {
        return Collections.unmodifiableList(new ArrayList<>(trades));
    }

    public synchronized void clear() {
        trades.clear();
    }

    public synchronized int size() {
        return trades.size();
    }
}
