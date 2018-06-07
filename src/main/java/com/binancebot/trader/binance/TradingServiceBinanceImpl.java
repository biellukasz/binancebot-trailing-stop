package com.binancebot.trader.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderType;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.*;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.market.OrderBook;
import com.binancebot.trader.TradingService;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TradingServiceBinanceImpl implements TradingService {

    private static Logger logger = LoggerFactory.getLogger(TradingServiceBinanceImpl.class);

    private BinanceApiRestClient client;
    private String baseCurrency;
    private String tradeCurrency;
    private String symbol;
    private TextArea logBox;


    public TradingServiceBinanceImpl(String baseCurrency, String tradeCurrency, String key, String secret,TextArea logBox) {
        this.baseCurrency = baseCurrency;
        this.tradeCurrency = tradeCurrency;
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(key, secret);
        client = factory.newRestClient();
        symbol = tradeCurrency + baseCurrency;
        this.logBox = logBox;

    }

    @Override
    public double  getLastesPrice() {
        return Double.valueOf(client.get24HrPriceStatistics(symbol).getLastPrice());
    }

    @Override
    public NewOrderResponse buy(double procent, double price) {
        String priceString = String.format("%.8f", price).replace(",", ".");
        Account account = client.getAccount();
        String btc = account.getAssetBalance("BTC").getFree();
        Double aDouble = Double.valueOf(btc);
        Double enj = Double.valueOf(client.get24HrPriceStatistics(symbol).getLastPrice());
        String quantity = String.valueOf((int) ((aDouble / enj) * procent));
        logger.info(String.format("Buying for %s\n", priceString));
        NewOrder order = new NewOrder(symbol, OrderSide.BUY, OrderType.LIMIT, TimeInForce.GTC, "" + quantity, priceString);
        return client.newOrder(order);
    }


    @Override
    public void sell(double quantity, double price) {
        String priceString = String.format("%.8f", price).replace(",", ".");
        logger.info(String.format("Selling %f for %s\n", quantity, priceString));
        NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.LIMIT, TimeInForce.GTC, "" + quantity, priceString);
        client.newOrder(order);
    }

    @Override
    public void sellMarket() {
            logger.info("Stop Lose Market Sell ");
            NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.MARKET, null, "" + Double.valueOf(getTradingBalance().getFree()).intValue());
            client.newOrder(order);
    }

    @Override
    public Long stopLoseOrder(double price,double stopLosePrice) {
        logger.info("Stop Lose Order ");
        String priceString = String.format("%.8f", price).replace(",", ".");
        String stopLosePriceString = String.format("%.8f", stopLosePrice).replace(",", ".");
        NewOrder order = new NewOrder(symbol, OrderSide.SELL, OrderType.STOP_LOSS_LIMIT, TimeInForce.GTC,""+ Double.valueOf(getTradingBalance().getFree()).intValue(), priceString);
        NewOrderResponse newOrderResponse = client.newOrder(order.stopPrice(stopLosePriceString));
        return newOrderResponse.getOrderId();
    }


    public OrderBook getOrderBook() {
        return client.getOrderBook(symbol, 5);
    }

    public void cancelAllOrders() {
        getOpenOrders().forEach(order -> client.cancelOrder(new CancelOrderRequest(symbol, order.getOrderId())));
    }

    public List<Order> getOpenOrders() {
        OrderRequest request = new OrderRequest(symbol);
        return client.getOpenOrders(request);
    }
    public AssetBalance getTradingBalance() {
        return client.getAccount().getAssetBalance(tradeCurrency);
    }

    public Order getOrder(long orderId) {
        return client.getOrderStatus(new OrderStatusRequest(symbol, orderId));
    }

    public void cancelOrder(long orderId) {
        logger.info("Cancelling order " + orderId);
        client.cancelOrder(new CancelOrderRequest(symbol, orderId));
    }

    public static void main(String[] args) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("hdqjtgAKZUireocZaDVLuj1qu4BEhvhmV2PrWoCAWYeW9TIwGpeD2d3ffOTKpGlB", "vMcq8Gu2q6l8fYEP1D9ZdcyaVyF7CLeanJrh749BKA81zYAX0IGkL5EFnh0WfzC0");
        BinanceApiRestClient binanceApiRestClient = factory.newRestClient();
        Account account = binanceApiRestClient.getAccount();
        String btc = account.getAssetBalance("BTC").getFree();
        Double aDouble = Double.valueOf(btc);
        Double enj = Double.valueOf(binanceApiRestClient.get24HrPriceStatistics("ENJBTC").getLastPrice());
        int i = (int) ((aDouble / enj) * 0.5);



        System.out.println(String.valueOf(i));

    }
}
