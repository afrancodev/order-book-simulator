# 📊 Order Book Simulator

This project is a Java-based **order book simulator** for modeling real-time trading, matching engine behavior, and order flow dynamics in financial markets.

---

## Simulation Example

![App Demo](order_book_simulator.gif)

---

## 🔧 Features

- 🛒 Support for **limit** and **market** orders
- 🔁 Real-time **order matching engine**
- 📈 Tracks **best bid/ask**, **mid price**, and price history
- 📦 Configurable **order generation** with randomized behavior
- 🧼 Order expiration logic based on age
- 🪣 Aggregated price levels (depth)
- 🧾 Trade recording with price & quantity
- ☕ Pure Java — no external dependencies
---

## 📁 Project Structure

```
src/
├── com.afrancodev.orderbook/
│   ├── OrderBookSimulator.java   # Application entry point
│   ├── SimulationEngine.java     # Application main loop
│   ├── OrderBook.java            # Order management
│   └── OrderGenerator.java       # Random order generator
│
├── com.afrancodev.orderbook.models/
│   ├── Order.java                # Order representation
|   ├── Trade.java                # Trade model
│   ├── TradeHistory.java         # Trade History model
│   └── PriceLevelData.java       # Aggregator of Prices and Quantities
│
├── com.afrancodev.orderbook.ui/
│   ├── MainPanel.java            # Main Ui Panel
|   ├── OrderBookPanel.java       # Order Book Panel 
│   └── PriceChartPanel.java      # Price Chart Panel
```

---

## 🛠️ Requirements

- Java 21 
- No external libraries needed

---
## How to run

```bash
mvn clean compile exec:java
```

---

## Some ideas on the roadmap:
- 🕯️ Candlestick chart type  
- ⏸️ Pause/resume simulation  
- ⚙️ Settings panel (spread, speed, order size)  
- 📊 Global metrics (order stats, time elapsed)
---

## Credits

Created by [afrancodev](https://github.com/afrancodev) — Open source ❤️

Feel free to fork this repository and add your own changes!

---

## License

This project is licensed under the MIT License.
