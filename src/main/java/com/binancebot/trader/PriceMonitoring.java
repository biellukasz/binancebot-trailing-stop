package com.binancebot.trader;

import com.binancebot.trader.model.CurrencyChanges;

import java.util.List;

public interface PriceMonitoring {

    List<CurrencyChanges> statistic ();
}
