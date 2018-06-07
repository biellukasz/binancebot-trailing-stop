package com.binancebot.controller;

import com.binancebot.trader.binance.BinancePriceMonitoringImpl;
import com.binancebot.trader.binance.BinanceTraderImpl;
import com.binancebot.trader.binance.BinanceTraderImplBuilder;
import com.binancebot.trader.model.CurrencyChanges;
import de.felixroske.jfxsupport.FXMLController;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;


@FXMLController
public class MainViewController {



    @FXML
    TextField apiKey,secretKey,cryptoSymbol,tradeAmount,stopLose,takeProfit;

    @FXML
    TextArea log,bestCurrency;

    @FXML
    Label boughtPrice,currentPrice;

    private ScheduledService<String> svc;

    private ScheduledService<String> priceMonitoringTask;

    private ScheduledService<String> getBestCurrency;



    @FXML
    public void pingBinance(ActionEvent event) {
        BinanceTraderImplBuilder binanceTraderImplBuilder = new BinanceTraderImplBuilder();
        binanceTraderImplBuilder.setBaseCurrency("BTC");
        binanceTraderImplBuilder.setTradeCurrency(cryptoSymbol.getText().toUpperCase().trim());
        binanceTraderImplBuilder.setKey(apiKey.getText().trim());
        binanceTraderImplBuilder.setSecret(secretKey.getText().trim());
        binanceTraderImplBuilder.setLogbox(log);
        binanceTraderImplBuilder.setBoughtPrice(boughtPrice);
        binanceTraderImplBuilder.setCurrentPrice(currentPrice);
        binanceTraderImplBuilder.setTradeAmount(Double.valueOf(tradeAmount.getText().trim()));
        binanceTraderImplBuilder.setProfit(Double.valueOf(takeProfit.getText().trim()));
        binanceTraderImplBuilder.setStopLose(Double.valueOf(stopLose.getText().trim()));
        BinanceTraderImpl binanceTrader = binanceTraderImplBuilder.createBinanceTrader();


        svc = new ScheduledService<String>() {
            protected Task<String> createTask() {
                return new Task<String>() {
                    protected String call() {
                        try{
                            binanceTrader.tick();
                        }catch (Exception e){
                            log.setText(log.getText()+"\n"+"Trading Canceled  " + e.getMessage());
                            log.appendText("");
                        }
                        return "succes";
                    }
                };
            }
        };
        svc.setPeriod(Duration.seconds(3));
        svc.start();
    }
    @FXML
    public void stopTrading(ActionEvent event){
        svc.cancel();
        log.setText(log.getText()+"\n"+"Trading Canceled");
        log.appendText("");
    }
    @FXML
    public void priceMonitoring(ActionEvent event) {
        BinancePriceMonitoringImpl binancePriceMonitoring = new BinancePriceMonitoringImpl(apiKey.getText().trim(),
                secretKey.getText().trim());

        priceMonitoringTask = new ScheduledService<String>() {
            protected Task<String> createTask() {
                return new Task<String>() {
                    protected String call() {
                        binancePriceMonitoring.statistic();
                        return "succes";
                    }
                };
            }
        };
        priceMonitoringTask.setPeriod(Duration.seconds(60));
        priceMonitoringTask.start();


        getBestCurrency = new ScheduledService<String>() {
            protected Task<String> createTask() {
                return new Task<String>() {
                    protected String call() {
                        CurrencyChanges increasingCurrency = binancePriceMonitoring.getIncreasingCurrency();
                        bestCurrency.setText(bestCurrency.getText()+"\n"+increasingCurrency.getCurrencySymbol() + " " + increasingCurrency.getProcentageChangeSum());
                        bestCurrency.appendText("");
                        return "succes";
                    }
                };
            }
        };
        getBestCurrency.setPeriod(Duration.minutes(4));
        getBestCurrency.start();
    }
}
