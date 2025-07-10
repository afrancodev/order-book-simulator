package com.afrancodev.orderbook.ui;

import javax.swing.*;
import java.awt.*;
import com.afrancodev.orderbook.OrderBook;

public class MainPanel extends JPanel {

    public MainPanel(OrderBook orderBook) {
        
        setLayout(new BorderLayout());
        
        PriceChartPanel priceChartPanel = new PriceChartPanel(orderBook, 700, 450);
        OrderBookPanel orderBookPanel = new OrderBookPanel(orderBook, 500, 450);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(orderBookPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                priceChartPanel,
                rightPanel
        );

        splitPane.setDividerLocation(700);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);    

        add(splitPane, BorderLayout.CENTER);
    }

}
