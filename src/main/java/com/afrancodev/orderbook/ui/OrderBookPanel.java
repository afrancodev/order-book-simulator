package com.afrancodev.orderbook.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.afrancodev.orderbook.OrderBook;
import com.afrancodev.orderbook.models.PriceLevelData;

public class OrderBookPanel extends JPanel {

    private enum OrderType {
        BID, ASK
    }
    private final OrderBook orderBook;

    private static final int PADDING_TOP = 30;
    private static final int ROW_HEIGHT = 20;
    private static final int MAX_BAR_WIDTH = 90; // 140 - 50
    private static final int MAX_QTY_FOR_FULL_BAR = 50;
    private static final int COLUMN_WIDTH = 175;
    private static final int MAX_ROWS = 20;

    private final int panelWidth;
    private final int panelHeight;

    public OrderBookPanel(OrderBook orderBook, int width, int height) {
        this.orderBook = orderBook;
        this.panelWidth = width;
        this.panelHeight = height;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        orderBook.expireOldOrders();
        orderBook.updatePrices();

        PriceLevelData bidLevels = orderBook.getBidLevels();
        PriceLevelData askLevels = orderBook.getAskLevels();

        List<Double> bidPrices = bidLevels.prices;
        List<Integer> bidQuantities = bidLevels.quantities;
        List<Double> askPrices = askLevels.prices;
        List<Integer> askQuantities = askLevels.quantities;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintBackground(g2);
        paintTitles(g2);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i < MAX_ROWS; i++) {
            int y = PADDING_TOP + i * ROW_HEIGHT;

            if (i < bidPrices.size() && i < bidQuantities.size()) {
                drawOrderBar(g2, fm, bidQuantities.get(i), bidPrices.get(i), y, OrderType.BID);
            }

            if (i < askPrices.size() && i < askQuantities.size()) {
                drawOrderBar(g2, fm, askQuantities.get(i), askPrices.get(i), y, OrderType.ASK);
            }
        }
    }

    private void paintBackground(Graphics2D g2) {
        g2.setColor(new Color(240, 240, 240));
        g2.fillRect(0, 0, panelWidth, panelHeight);
    }

    private void paintTitles(Graphics2D g2) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();

        String bidTitle = "Buy Orders";
        String askTitle = "Sell Orders";

        int bidTitleWidth = fm.stringWidth(bidTitle);
        g2.setColor(Color.BLUE.darker());
        g2.drawString(bidTitle, COLUMN_WIDTH - bidTitleWidth, 20);

        g2.setColor(Color.RED.darker());
        g2.drawString(askTitle, COLUMN_WIDTH + 5, 20);
    }

    private void drawOrderBar(Graphics2D g2, FontMetrics fm, int quantity, double price, int y, OrderType type) {
        double normalizedQty = Math.min(quantity, MAX_QTY_FOR_FULL_BAR) / (double) MAX_QTY_FOR_FULL_BAR;
        int barWidth = (int) (normalizedQty * MAX_BAR_WIDTH);
        int minBarWidth = (type == OrderType.BID) ? 10 : 15;
        barWidth = Math.max(barWidth, minBarWidth);

        int barX, textX;
        String text;

        if (type == OrderType.BID) {
            barX = COLUMN_WIDTH - barWidth;
            g2.setColor(new Color(0, 0, 255, 100));
            g2.fillRect(barX, y + 4, barWidth, ROW_HEIGHT - 6);

            g2.setColor(Color.BLUE.darker());
            text = String.format("%d | %.2f", quantity, price);
            int textWidth = fm.stringWidth(text);
            textX = barX - 5 - textWidth;
        } else {
            barX = COLUMN_WIDTH + 5;
            g2.setColor(new Color(255, 0, 0, 100));
            g2.fillRect(barX, y + 4, barWidth, ROW_HEIGHT - 6);

            g2.setColor(Color.RED.darker());
            text = String.format("%.2f | %d", price, quantity);
            textX = barX + 5 + barWidth;
        }

        g2.drawString(text, textX, y + 16);
    }
}
