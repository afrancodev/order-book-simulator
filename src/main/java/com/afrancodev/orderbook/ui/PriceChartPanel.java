package com.afrancodev.orderbook.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.afrancodev.orderbook.OrderBook;

public class PriceChartPanel extends JPanel {

    private final OrderBook orderBook;

    private final int width;
    private final int height;

    private static final int PADDING_LEFT = 50;
    private static final int PADDING_BOTTOM = 40;
    private static final int PADDING_TOP = 30;
    private static final int PADDING_RIGHT = 20;

    private static final int GRID_LINES = 5;
    private static final int MIN_SPACING = 2;

    private final JCheckBox bidCheckbox;
    private final JCheckBox askCheckbox;
    private final JCheckBox midCheckbox;

    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color GRID_COLOR = new Color(200, 200, 200);
    private static final Color AXIS_LABEL_COLOR = Color.DARK_GRAY;

    private static final Font AXIS_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
    private static final Font SERIES_LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font SAMPLE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    public PriceChartPanel(OrderBook orderBook, int width, int height) {
        this.orderBook = orderBook;
        this.width = width;
        this.height = height;

        bidCheckbox = new JCheckBox("Show Bids", true);
        askCheckbox = new JCheckBox("Show Asks", true);
        midCheckbox = new JCheckBox("Show Mids", true);

        bidCheckbox.addActionListener(e -> repaint());
        askCheckbox.addActionListener(e -> repaint());
        midCheckbox.addActionListener(e -> repaint());

        JPanel checkboxPanel = createCheckboxPanel();
        setLayout(new BorderLayout());
        add(checkboxPanel, BorderLayout.SOUTH);
    }

    private JPanel createCheckboxPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(bidCheckbox);
        panel.add(askCheckbox);
        panel.add(midCheckbox);
        return panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Double> bids = orderBook.getBidHistory();
        List<Double> asks = orderBook.getAskHistory();
        List<Double> mids = orderBook.getMidHistory();

        if (mids.isEmpty()) {
            return; 
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final int plotWidth = width - PADDING_LEFT - PADDING_RIGHT;
        final int plotHeight = height - PADDING_TOP - PADDING_BOTTOM;

        // Combine all prices for min/max calculation
        List<Double> allPrices = new ArrayList<>();
        addNonNull(allPrices, bids);
        addNonNull(allPrices, asks);
        addNonNull(allPrices, mids);

        if (allPrices.isEmpty()) {
            g2.dispose();
            return;
        }

        double minPrice = Collections.min(allPrices) * 0.999;
        double maxPrice = Collections.max(allPrices) * 1.001;
        double priceRange = maxPrice - minPrice;
        if (priceRange == 0) priceRange = 1;

        int spacing = Math.max(plotWidth / mids.size(), MIN_SPACING);

        drawBackground(g2, plotWidth, plotHeight);
        drawGridLinesAndLabels(g2, plotWidth, plotHeight, maxPrice, priceRange);
        drawSampleIndices(g2, mids.size(), spacing, plotHeight);

        // Draw price series conditionally
        if (bidCheckbox.isSelected()) {
            drawSeries(g2, bids, spacing, plotHeight, minPrice, priceRange, PADDING_TOP, Color.BLUE, true);
            drawPoints(g2, bids, spacing, plotHeight, minPrice, priceRange, PADDING_TOP, new Color(0, 0, 255, 180));
        }
        if (askCheckbox.isSelected()) {
            drawSeries(g2, asks, spacing, plotHeight, minPrice, priceRange, PADDING_TOP, Color.GREEN.darker(), true);
            drawPoints(g2, asks, spacing, plotHeight, minPrice, priceRange, PADDING_TOP, new Color(0, 128, 0, 180));
        }
        if (midCheckbox.isSelected()) {
            drawSeries(g2, mids, spacing, plotHeight, minPrice, priceRange, PADDING_TOP, Color.RED, true);
            drawPoints(g2, mids, spacing, plotHeight, minPrice, priceRange, PADDING_TOP, new Color(255, 0, 0, 180));
        }

        drawLegend(g2);

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2, int plotWidth, int plotHeight) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(PADDING_LEFT, PADDING_TOP, plotWidth, plotHeight);
    }

    private void drawGridLinesAndLabels(Graphics2D g2, int plotWidth, int plotHeight,
                                        double maxPrice, double priceRange) {
        g2.setColor(GRID_COLOR);
        g2.setFont(AXIS_LABEL_FONT);

        for (int i = 0; i <= GRID_LINES; i++) {
            int y = PADDING_TOP + i * plotHeight / GRID_LINES;
            g2.drawLine(PADDING_LEFT, y, PADDING_LEFT + plotWidth, y);

            double priceLabel = maxPrice - i * priceRange / GRID_LINES;
            String label = String.format("%.2f", priceLabel);

            g2.setColor(AXIS_LABEL_COLOR);
            g2.drawString(label, 5, y + 5);
            g2.setColor(GRID_COLOR);
        }
    }

    private void drawSampleIndices(Graphics2D g2, int dataSize, int spacing, int plotHeight) {
        long firstSampleIndex = orderBook.getFirstSampleIndex();

        g2.setColor(AXIS_LABEL_COLOR);
        g2.setFont(SAMPLE_LABEL_FONT);

        for (int i = 0; i < dataSize; i++) {
            long sampleIndex = firstSampleIndex + i;
            if (sampleIndex % 100 != 0) continue;

            int x = PADDING_LEFT + i * spacing;
            int y = PADDING_TOP + plotHeight + 15;

            g2.drawString(String.valueOf(sampleIndex), x - 6, y);
        }
    }

    private void drawLegend(Graphics2D g2) {
        g2.setFont(SERIES_LABEL_FONT);

        g2.setColor(Color.BLUE);
        String bidStr = "Bid: " + (orderBook.getCurrentBid() != null ? orderBook.getCurrentBid() : "—");
        g2.drawString(bidStr, PADDING_LEFT, 20);

        g2.setColor(Color.RED);
        String midVal = orderBook.getCurrentMid() != null ?
                String.format("%.2f", orderBook.getCurrentMid()) : "—";
        g2.drawString("Mid: " + midVal, PADDING_LEFT + 90, 20);

        g2.setColor(Color.GREEN.darker());
        String askStr = "Ask: " + (orderBook.getCurrentAsk() != null ? orderBook.getCurrentAsk() : "—");
        g2.drawString(askStr, PADDING_LEFT + 190, 20);
    }

    private void addNonNull(List<Double> target, List<Double> source) {
        for (Double d : source) {
            if (d != null) {
                target.add(d);
            }
        }
    }

    private void drawSeries(Graphics2D g2, List<Double> series, int spacing, int plotHeight,
                            double min, double range, int paddingTop, Color color, boolean thick) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thick ? 2.5f : 1f));

        int prevX = -1, prevY = -1;
        for (int i = 0; i < series.size(); i++) {
            Double price = series.get(i);
            if (price == null) continue;

            int x = PADDING_LEFT + i * spacing;
            int y = scaleY(price, min, range, plotHeight, paddingTop);

            if (prevX != -1) {
                g2.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
        }
    }

    private void drawPoints(Graphics2D g2, List<Double> series, int spacing, int plotHeight,
                            double min, double range, int paddingTop, Color color) {
        g2.setColor(color);
        int radius = 5;
        for (int i = 0; i < series.size(); i++) {
            Double price = series.get(i);
            if (price == null) continue;

            int x = PADDING_LEFT + i * spacing;
            int y = scaleY(price, min, range, plotHeight, paddingTop);

            g2.fillOval(x - radius / 2, y - radius / 2, radius, radius);
        }
    }

    private int scaleY(double price, double min, double range, int plotHeight, int paddingTop) {
        double normalized = (price - min) / range;
        return paddingTop + (int) ((1 - normalized) * plotHeight);
    }
}
