package com.menezo.assetsproject.model.dto;

public class AssetDTO {

    private String longName;
    private double regularMarketPrice;
    private String symbol;

    public AssetDTO(String longName, double regularMarketPrice, String symbol) {
        this.longName = longName;
        this.regularMarketPrice = regularMarketPrice;
        this.symbol = symbol;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public double getRegularMarketPrice() {
        return regularMarketPrice;
    }

    public void setRegularMarketPrice(double regularMarketPrice) {
        this.regularMarketPrice = regularMarketPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
