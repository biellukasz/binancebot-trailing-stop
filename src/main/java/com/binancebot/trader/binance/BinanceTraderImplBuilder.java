package com.binancebot.trader.binance;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class BinanceTraderImplBuilder {
    private TextArea logbox;
    private String baseCurrency, tradeCurrency, key, secret;
    private Label boughtPrice, currentPrice;
    private double profit;
    private double stopLose;
    private double tradeAmount;
    private Double stopLosePriceProcent;

    public BinanceTraderImplBuilder() {
    }

    public void setLogbox(TextArea logbox) {
        this.logbox = logbox;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setTradeCurrency(String tradeCurrency) {
        this.tradeCurrency = tradeCurrency;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setBoughtPrice(Label boughtPrice) {
        this.boughtPrice = boughtPrice;
    }

    public void setCurrentPrice(Label currentPrice) {
        this.currentPrice = currentPrice;
    }



    public void setProfit(double profit) {
        this.profit = profit;
    }

    public void setStopLose(double stopLose) {
        this.stopLose = stopLose;
    }

    public void setTradeAmount(double tradeAmount) {
        this.tradeAmount = tradeAmount;
    }


    public void setStopLosePriceProcent(Double stopLosePriceProcent) {
        this.stopLosePriceProcent = stopLosePriceProcent;
    }

    public BinanceTraderImpl createBinanceTrader() {
        return new BinanceTraderImpl(baseCurrency, tradeCurrency, key, secret, logbox, boughtPrice, currentPrice, tradeAmount, profit, stopLose, stopLosePriceProcent);
    }




}

