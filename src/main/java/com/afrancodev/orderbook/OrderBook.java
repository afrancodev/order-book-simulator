package com.afrancodev.orderbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import com.afrancodev.orderbook.models.Order;
import com.afrancodev.orderbook.models.PriceLevelData;
import com.afrancodev.orderbook.models.Trade;
import com.afrancodev.orderbook.models.TradeHistory;

public class OrderBook {

    private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(
            Comparator.comparingDouble(Order::getPrice).reversed()
    );

    private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(
            Comparator.comparingDouble(Order::getPrice)
    );

    private final Object lock = new Object();
    private final List<Double> bidHistory = new ArrayList<>();
    private final List<Double> askHistory = new ArrayList<>();
    private final List<Double> midHistory = new ArrayList<>();

    private static final int MAX_POINTS = 250;
    private long totalSamples = 0;
    private Double bestBid = null;
    private Double bestAsk = null;
    private Double midPrice = null;

    private final TradeHistory tradeHistory = new TradeHistory();

    public void addOrder(Order order) {
        synchronized (lock) {
            if (order.isBuy()) {
                buyOrders.offer(order);
            } else {
                sellOrders.offer(order);
            }
        }
    }

    public void matchOrders() {
        synchronized (lock) {
            while (!buyOrders.isEmpty() && !sellOrders.isEmpty()) {
                Order buy = buyOrders.peek();
                Order sell = sellOrders.peek();

                if (buy.isMarketOrder() && sell.isMarketOrder()) {
                    break;
                }

                boolean canMatch = buy.isMarketOrder() || sell.isMarketOrder() || buy.getPrice() >= sell.getPrice();
                if (!canMatch) {
                    break;
                }

                int tradedQty = Math.min(buy.getQuantity(), sell.getQuantity());
                double tradePrice = determineTradePrice(buy, sell);

                tradeHistory.recordTrade(new Trade(true, tradePrice, tradedQty));

                buy.reduceQuantity(tradedQty);
                sell.reduceQuantity(tradedQty);

                if (buy.getQuantity() == 0) {
                    buyOrders.poll();
                }
                if (sell.getQuantity() == 0) {
                    sellOrders.poll();
                }
            }
        }
    }

    private double determineTradePrice(Order buy, Order sell) {
        if (buy.isMarketOrder() && !sell.isMarketOrder()) {
            return sell.getPrice();
        }
        if (!buy.isMarketOrder() && sell.isMarketOrder()) {
            return buy.getPrice();
        }
        return sell.getPrice(); // or midpoint logic if desired
    }

    public void expireOldOrders() {
        synchronized (lock) {
            expireQueue(buyOrders);
            expireQueue(sellOrders);
        }
    }

    private void expireQueue(PriorityQueue<Order> queue) {
        for (Order order : queue) {
            order.decreaseAge();
        }

        Iterator<Order> it = queue.iterator();
        while (it.hasNext()) {
            if (it.next().isExpired()) {
                it.remove();
            }
        }
    }

    public void updatePrices() {
        synchronized (lock) {
            bestBid = buyOrders.isEmpty() ? null : buyOrders.peek().getPrice();
            bestAsk = sellOrders.isEmpty() ? null : sellOrders.peek().getPrice();
            midPrice = (bestBid != null && bestAsk != null) ? (bestBid + bestAsk) / 2 : null;

            addToHistory(bidHistory, bestBid);
            addToHistory(askHistory, bestAsk);
            addToHistory(midHistory, midPrice);

            totalSamples++;
        }
    }

    private void addToHistory(List<Double> history, Double value) {
        history.add(value);
        if (history.size() > MAX_POINTS) {
            history.remove(0);
        }
    }

    public PriceLevelData getBidLevels() {
        return aggregateLevels(buyOrders, true);
    }

    public PriceLevelData getAskLevels() {
        return aggregateLevels(sellOrders, false);
    }

    private PriceLevelData aggregateLevels(PriorityQueue<Order> orders, boolean descending) {
        Map<Double, Integer> map = new TreeMap<>(descending ? Collections.reverseOrder() : Comparator.naturalOrder());
        synchronized (lock) {
            for (Order o : orders) {
                map.merge(o.getPrice(), o.getQuantity(), Integer::sum);
            }
        }
        return new PriceLevelData(new ArrayList<>(map.keySet()), new ArrayList<>(map.values()));
    }

    public Double getCurrentBid() {
        return bestBid;
    }

    public Double getCurrentAsk() {
        return bestAsk;
    }

    public Double getCurrentMid() {
        return midPrice;
    }

    public List<Double> getBidHistory() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<>(bidHistory));
        }
    }

    public List<Double> getAskHistory() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<>(askHistory));
        }
    }

    public List<Double> getMidHistory() {
        synchronized (lock) {
            return Collections.unmodifiableList(new ArrayList<>(midHistory));
        }
    }

    public long getFirstSampleIndex() {
        synchronized (lock) {
            return totalSamples - midHistory.size() + 1;
        }
    }

    public int getBuyOrdersCount() {
        synchronized (lock) {
            return buyOrders.size();
        }
    }

    public int getSellOrdersCount() {
        synchronized (lock) {
            return sellOrders.size();
        }
    }

    public int getTotalBuyOrdersQuantity() {
        synchronized (lock) {
            return buyOrders.stream().mapToInt(Order::getQuantity).sum();
        }
    }

    public int getTotalSellOrdersQuantity() {
        synchronized (lock) {
            return sellOrders.stream().mapToInt(Order::getQuantity).sum();
        }
    }

    public List<Trade> getTradeHistory() {
        return tradeHistory.getAllTrades();
    }

    public List<Integer> getBidQuantities() {
        return getBidLevels().quantities;
    }

    public List<Integer> getAskQuantities() {
        return getAskLevels().quantities;
    }

}
