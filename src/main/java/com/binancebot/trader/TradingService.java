package com.binancebot.trader;


import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.OrderBook;

public interface TradingService {

    double getLastesPrice();

    NewOrderResponse buy(double quantity, double price);

    void sell(double quantity, double price);

    void sellMarket();

    OrderBook getOrderBook();


}
