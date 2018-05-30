package com.binancebot.trader.binance;

import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.Order;
import com.binance.api.client.domain.market.OrderBook;
import com.binancebot.trader.Trader;
import com.binancebot.utils.Utils;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinanceTraderImpl implements Trader {

    private static Logger logger = LoggerFactory.getLogger(BinanceTraderImpl.class);


    private TextArea logbox;
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



    public BinanceTraderImpl(String baseCurrency, String tradeCurrency, String key, String secret,TextArea logbox,double tradeAmount,double profit,double stopLose) {
        tradingServiceBinance = new TradingServiceBinanceImpl(baseCurrency,tradeCurrency,key,secret,logbox);
        this.priceToTrack = tradingServiceBinance.getLastesPrice();
        this.profit = profit;
        this.stopLose = stopLose;
        this.logbox = logbox;
        this.tradeAmount = tradeAmount;
        this.stopLosePrice = priceToTrack - (priceToTrack * stopLose/100);
        utils = new Utils();
    }

    @Override
    public void tick() {
        double profitablePrice;
        double lastprice = tradingServiceBinance.getLastesPrice();
        OrderBook orderBook = tradingServiceBinance.getOrderBook();
        double lastBid = Double.valueOf(orderBook.getBids().get(0).getPrice());
        double lastAsk = Double.valueOf(orderBook.getAsks().get(0).getPrice());
        if(lastBid > priceToTrack){
             stopLosePrice = lastBid - (lastBid * stopLose / 100);
             priceToTrack = lastBid;
        }
        if(curentlyBoughtPrice == 0) {
             profitablePrice = lastBid + (lastBid * profit / 100);
        }else {
             profitablePrice = curentlyBoughtPrice + (curentlyBoughtPrice * profit / 100);
        }

        try {
            if(trades == 2 ){
                utils.addLogLine(logbox,"StopLose triggered and performed");
            }
            if (trades == 0 && orderId == null) {
                if (lastprice <= stopLosePrice) {
                    utils.addLogLine(logbox, "Do nothing price is droping");
                } else {
                    orderId = tradingServiceBinance.buy(tradeAmount, lastAsk).getOrderId();
                    trades = 1;
                    curentlyBoughtPrice = lastAsk;
                    utils.addLogLine(logbox, String.format("Bought for %.8f",curentlyBoughtPrice));
                }
            }else  {
                Order order = tradingServiceBinance.getOrder(orderId);
                OrderStatus status = order.getStatus();
                if (lastBid >= profitablePrice && status == OrderStatus.FILLED) {
                    tradingServiceBinance.sell(tradeAmount, lastBid);
                    utils.addLogLine(logbox, String.format("Sell for lastBid:%.8f with profit:%.8f ",lastBid,lastBid-curentlyBoughtPrice));
                    trades = 0;
                    curentlyBoughtPrice = 0;
                    orderId = null;
                }
                if (lastBid <= stopLosePrice && trades == 1 &&status == OrderStatus.FILLED) {
                    tradingServiceBinance.sellMarket();
                    trades = 2;
                }
                utils.addLogLine(logbox,String.format("Stop Lose price is   %.8f Current Price is  %.8f",stopLosePrice,lastBid));
            }
        }catch (Exception e ){
            logger.error("Unable to perform ticker exception : " + e.getMessage(), e);
            utils.addLogLine(logbox,e.getMessage());
        }
    }
}
