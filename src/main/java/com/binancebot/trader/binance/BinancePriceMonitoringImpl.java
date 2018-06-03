package com.binancebot.trader.binance;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import com.binancebot.trader.PriceMonitoring;
import com.binancebot.trader.model.CurrencyChanges;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Comparator.comparing;

public class BinancePriceMonitoringImpl implements PriceMonitoring {

    private final static Logger logger = LoggerFactory.getLogger(BinanceTraderImpl.class);

    private BinanceApiRestClient client;

    private List<CurrencyChanges> statistic;

    private int checks;



    public BinancePriceMonitoringImpl(String key, String secret ) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(key, secret);
        client = factory.newRestClient();
        initStatistic();

    }

    @Override
    public List<CurrencyChanges> statistic() {
        updateStatistics();
        return statistic;
    }

    private void initStatistic(){
        if(statistic == null){
            statistic = new ArrayList<>();
            checks = 0;
            logger.info("Statistic are empty creating");
            List<TickerPrice> allPrices = client.getAllPrices();
            for(TickerPrice tickerPrice : allPrices){
                if(tickerPrice.getSymbol().contains("BTC")) {
                    Double price = Double.valueOf(tickerPrice.getPrice());
                    CurrencyChanges currencyChanges = new CurrencyChanges(tickerPrice.getSymbol().replace("BTC", ""), price, 0.0D,0.0);
                    statistic.add(currencyChanges);
                }
            }
        }
    }

    private void updateStatistics(){
        if(statistic != null){
            List<TickerPrice> allPrices = client.getAllPrices();
            List<TickerPrice> tickerPrices = filtrForBtcPriceList(allPrices);
            checks++;
            logger.info("Updating statistics checks number is " + checks);

            for(int i =0;i<=statistic.size();i++){
                TickerPrice tickerPrice = tickerPrices.get(i);
                CurrencyChanges currencyChanges = statistic.get(i);
                Double oldPrice = currencyChanges.getPrice();
                Double newPrice = Double.valueOf(tickerPrice.getPrice());
                Double procentDif = (((newPrice - oldPrice)/oldPrice)*100);
                currencyChanges.setPrice(newPrice);
                currencyChanges.setProcentageChange(procentDif);
                if(checks < 4) {
                    currencyChanges.setProcentageChangeSum(currencyChanges.getProcentageChange() + procentDif);
                }else {
                    currencyChanges.setProcentageChangeSum(0.0);
                }
            }

        }
        if(checks == 4){
            checks =0;
        }

    }
    private List<TickerPrice>  filtrForBtcPriceList(List<TickerPrice> tickerPrices){
        List<TickerPrice> btcPriceList = new ArrayList<>();
        for(TickerPrice tickerPrice : tickerPrices){
            if(tickerPrice.getSymbol().contains("BTC")) {
                btcPriceList.add(tickerPrice);
            }
        }
        return btcPriceList;
    }
    public CurrencyChanges getIncreasingCurrency(){
        logger.info("Getting best Currency");
        statistic.sort(comparing(CurrencyChanges::getProcentageChangeSum));
        Collections.reverse(statistic);
        return statistic.get(0);

    }

    public List<CurrencyChanges> getStatistic() {
        return statistic;
    }

    public void setStatistic(List<CurrencyChanges> statistic) {
        this.statistic = statistic;
    }

}
