package com.afrancodev.orderbook;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.afrancodev.orderbook.ui.MainPanel;

public class OrderBookSimulator {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OrderBook orderBook = new OrderBook();
            MainPanel mainPanel = new MainPanel(orderBook);
            OrderGenerator orderGenerator = new OrderGenerator(100.0);

            JFrame frame = new JFrame("Order Book Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 500);
            frame.setResizable(false);
            frame.setVisible(true);
            frame.add(mainPanel);

            SimulationEngine engine = new SimulationEngine(orderBook, mainPanel, orderGenerator);
            new Thread(engine).start();
        });
    }
}
