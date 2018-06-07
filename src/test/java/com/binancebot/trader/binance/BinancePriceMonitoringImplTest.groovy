package com.binancebot.trader.binance

import spock.lang.Specification

class BinancePriceMonitoringImplTest extends Specification {

    def "should init currencystatist with basic data"() {
        given:
        BinancePriceMonitoringImpl binancePriceMonitoring = new BinancePriceMonitoringImpl("hdqjtgAKZUireocZaDVLuj1qu4BEhvhmV2PrWoCAWYeW9TIwGpeD2d3ffOTKpGlB","vMcq8Gu2q6l8fYEP1D9ZdcyaVyF7CLeanJrh749BKA81zYAX0IGkL5EFnh0WfzC0")
        when:
            binancePriceMonitoring.statistic()
        and:
            Thread.sleep(60000)
        and:
            binancePriceMonitoring.statistic()
        then:
            for(int i =0;i<binancePriceMonitoring.getStatistic().size();i++){
                if(binancePriceMonitoring.getStatistic().get(i).procentageChange >0){
                    System.out.println(binancePriceMonitoring.getStatistic().get(i).procentageChange + ' ' + binancePriceMonitoring.getStatistic().get(i).currencySymbol)

                }
            }

    }
    def "procentageSum check"(){
        given:
        BinancePriceMonitoringImpl binancePriceMonitoring = new BinancePriceMonitoringImpl("hdqjtgAKZUireocZaDVLuj1qu4BEhvhmV2PrWoCAWYeW9TIwGpeD2d3ffOTKpGlB","vMcq8Gu2q6l8fYEP1D9ZdcyaVyF7CLeanJrh749BKA81zYAX0IGkL5EFnh0WfzC0")
        when:
        binancePriceMonitoring.statistic()
        and:
        Thread.sleep(60000)
        and:
        binancePriceMonitoring.statistic()
        and:
        Thread.sleep(60000)
        and:
        binancePriceMonitoring.statistic()
        and:
        Thread.sleep(60000)
        and:
        binancePriceMonitoring.statistic()
        then:
        System.out.println(binancePriceMonitoring.increasingCurrency.currencySymbol)
    }
}
