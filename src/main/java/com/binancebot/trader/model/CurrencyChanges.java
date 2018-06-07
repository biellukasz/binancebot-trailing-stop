package com.binancebot.trader.model;


public class CurrencyChanges {

    private String currencySymbol;

    private Double price;

    private Double procentageChange;

    private Double procentageChangeSum;



    public CurrencyChanges(String currencySymbol, Double price, Double procentageChange, Double procentageChangeSum) {
        this.currencySymbol = currencySymbol;
        this.price = price;
        this.procentageChange = procentageChange;
        this.procentageChangeSum = procentageChangeSum;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getProcentageChange() {
        return procentageChange;
    }

    public void setProcentageChange(Double procentageChange) {
        this.procentageChange = procentageChange;
    }
    public Double getProcentageChangeSum() {
        return procentageChangeSum;
    }

    public void setProcentageChangeSum(Double procentageChangeSum) {
        this.procentageChangeSum = procentageChangeSum;
    }
}
