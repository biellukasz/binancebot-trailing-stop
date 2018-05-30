package com.binancebot.controller;

import com.binancebot.trader.binance.BinanceTraderImpl;
import de.felixroske.jfxsupport.FXMLController;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;


@FXMLController
public class MainViewController {



    @FXML
    TextField apiKey,secretKey,cryptoSymbol,tradeAmount,stopLose,takeProfit;

    @FXML
    TextArea log;

    private ScheduledService<String> svc;


    @FXML
    public void pingBinance(ActionEvent event) {
        BinanceTraderImpl binanceTrader = new BinanceTraderImpl("BTC",cryptoSymbol.getText().toUpperCase().trim(),apiKey.getText().trim(),
                                                                            secretKey.getText().trim(),log,Double.valueOf(tradeAmount.getText().trim()),
                                                                            Double.valueOf(takeProfit.getText().trim()),Double.valueOf(stopLose.getText().trim()));

        svc = new ScheduledService<String>() {
            protected Task<String> createTask() {
                return new Task<String>() {
                    protected String call() {
                        binanceTrader.tick();
//                        log.appendText("Tick tick");
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
}
