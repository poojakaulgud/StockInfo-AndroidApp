package com.example.assgn4;

public class FavoriteItem{
    private String ticker;
    private String description;
    private double currentPrice;
    private double d;
    private double dp;

    public FavoriteItem(String ticker, String description, double currentPrice, double d, double dp) {
        this.ticker = ticker;
        this.description = description;
        this.currentPrice = currentPrice;
        this.d = d;
        this.dp = dp;
    }

    public String getTicker(){
        return ticker;
    }

    public String getDesc(){
        return description;
    }
    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getD() {
        return d;
    }

    public double getDP() {
        return dp;
    }
//
//    public void setCurrentPrice(double currentPrice) {
//        this.currentPrice = currentPrice;
//    }
//
//    public double getChangePercentage() {
//        return changePercentage;
//    }
//
//    public void setChangePercentage(double changePercentage) {
//        this.changePercentage = changePercentage;
//    }
}
