package com.afrancodev.orderbook;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.afrancodev.orderbook.models.Order;
import com.afrancodev.orderbook.ui.MainPanel;

public class SimulationEngine implements Runnable {

    private final OrderBook orderBook;
    private final MainPanel mainPanel;
    private final OrderGenerator orderGenerator;

    private final ScheduledExecutorService scheduler;

    public SimulationEngine(OrderBook orderBook, MainPanel mainPanel, OrderGenerator orderGenerator) {
        this.orderBook = orderBook;
        this.mainPanel = mainPanel;
        this.orderGenerator = orderGenerator;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(() -> engineLoop(), 0, 100, TimeUnit.MILLISECONDS);
    }

    private void engineLoop() {
        orderGenerator.updateFairPrice();

        Order order = orderGenerator.generateRandomOrder(orderBook);

        orderBook.addOrder(order);
        orderBook.matchOrders();
        orderBook.updatePrices();
        orderBook.expireOldOrders();

        mainPanel.repaint();
    }
}
