package com.example.assgn4;

public class PortfolioItem{
    private String ticker;
    private int qty;
    private double totalCost;
    private double avgCost;
    private double currentPrice;
    private double d;
    private double dp;

    public PortfolioItem(String ticker, int qty, double totalCost,  double avgCost, double currentPrice, double d, double dp) {
        this.ticker = ticker;
        this.qty = qty;
        this.totalCost = totalCost;
        this.avgCost = avgCost;
        this.currentPrice = currentPrice;
        this.d = d;
        this.dp = dp;
    }

    public String getTicker(){
        return ticker;
    }

    public double getNumQty(){
        return qty;
    }
    public double getAvgCost(){
        return avgCost;
    }
    public double getTotalCost(){
        return totalCost;
    }
    public String getQty(){
        return qty + " Shares";
    }
    public double getCurrentPrice() {
        return currentPrice;
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
