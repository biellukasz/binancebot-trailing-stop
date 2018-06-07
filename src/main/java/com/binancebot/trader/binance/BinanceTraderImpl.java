package com.binancebot.trader.binance;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.market.OrderBook;
import com.binancebot.trader.Trader;
import com.binancebot.utils.Utils;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinanceTraderImpl implements Trader {

    private static Logger logger = LoggerFactory.getLogger(BinanceTraderImpl.class);


    private TextArea logbox;
    private Label boughtPrice ,currentPrice;
    private double priceToTrack;
    private double profit;
    private double stopLose;
    private double tradeAmount;
    private TradingServiceBinanceImpl tradingServiceBinance;
    private double trades = 0;
    private double curentlyBoughtPrice = 0;
    private Utils utils;
    private double stopLosePrice;
    private Long orderId;
    private Long stopLoseOrderId;
    private double priceStopLose;


    public BinanceTraderImpl(String baseCurrency, String tradeCurrency, String key, String secret, TextArea logbox, Label boughtPrice,Label currentPrice, double tradeAmount, double profit, double stopLose) {
        tradingServiceBinance = new TradingServiceBinanceImpl(baseCurrency, tradeCurrency, key, secret, logbox);
        this.priceToTrack = tradingServiceBinance.getLastesPrice();
        this.profit = profit;
        this.stopLose = stopLose;
        this.logbox = logbox;
        this.tradeAmount = tradeAmount;
        this.boughtPrice = boughtPrice;
        this.currentPrice = currentPrice;
        this.stopLosePrice = priceToTrack - (priceToTrack * stopLose / 100);
        utils = new Utils();
    }

    @Override
    public void tick() {
        double profitablePrice;
        double lastprice = tradingServiceBinance.getLastesPrice();
        OrderBook orderBook = tradingServiceBinance.getOrderBook();
        double lastBid = Double.valueOf(orderBook.getBids().get(0).getPrice());
        double lastAsk = Double.valueOf(orderBook.getAsks().get(0).getPrice());
        utils.updateLabelText(currentPrice,String.format("Current Price %.8f", lastBid));


        if (lastBid > priceToTrack) {
            stopLosePrice = lastBid - (lastBid * stopLose / 100);
            priceToTrack = lastBid;
            utils.addLogLine(logbox, "Canceling old stop lose");
            tradingServiceBinance.cancelOrder(stopLoseOrderId);
            priceStopLose = stopLosePrice - (stopLosePrice * 0.3 / 100);
            stopLoseOrderId = tradingServiceBinance.stopLoseOrder(priceStopLose, stopLosePrice);
            utils.addLogLine(logbox, "Created new stop lose order");
        }
        if (curentlyBoughtPrice == 0) {
            profitablePrice = lastBid + (lastBid * profit / 100);
        } else {
            profitablePrice = curentlyBoughtPrice + (curentlyBoughtPrice * profit / 100);
        }
        try {
            if (trades == 0 && orderId == null) {
                if (lastprice <= stopLosePrice) {
                    utils.addLogLine(logbox, "Do nothing price is droping");
                } else {
                    orderId = tradingServiceBinance.buy(tradeAmount, lastAsk).getOrderId();
                    trades = 1;
                    curentlyBoughtPrice = lastAsk;
                    utils.updateLabelText(boughtPrice, String.format("Bought for %.8f", curentlyBoughtPrice));
                    utils.addLogLine(logbox, String.format("Bought for %.8f", curentlyBoughtPrice));
                    priceStopLose = stopLosePrice - (stopLosePrice * 0.3 / 100);
                    if(tradingServiceBinance.getOrder(orderId).getStatus() == OrderStatus.FILLED){
                        stopLoseOrderId = tradingServiceBinance.stopLoseOrder(priceStopLose, stopLosePrice);
                    }
                }
            } else {
//                Order order = tradingServiceBinance.getOrder(orderId);
//                OrderStatus status = order.getStatus();
//                if (lastBid >= profitablePrice && status == OrderStatus.FILLED) {
//                    tradingServiceBinance.sell(tradeAmount, lastBid);
//                    utils.addLogLine(logbox, String.format("Sell for lastBid:%.8f with profit:%.8f ",lastBid,lastBid-curentlyBoughtPrice));
//                    trades = 0;
//                    curentlyBoughtPrice = 0;
//                    orderId = null;
//                }
//                if (lastBid <= stopLosePrice && trades == 1 &&status == OrderStatus.FILLED) {
//                    tradingServiceBinance.sellMarket();
//                    trades = 2;
//                }
                utils.addLogLine(logbox, String.format("Stop Lose price is   %.8f Current Price is  %.8f", stopLosePrice, lastBid));
            }
        } catch (Exception e) {
            logger.error("Unable to perform ticker exception : " + e.getMessage(), e);
            utils.addLogLine(logbox, e.getMessage());
        }
    }
}
