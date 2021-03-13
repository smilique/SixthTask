package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

public class Lot implements Runnable{

    private int id;
    private BigDecimal startPrice;
    private boolean sold;
    private BigDecimal price;
    private double quality;

    public Lot() {
    }

    public double getQuality() {
        return quality;
    }

    public synchronized void setQuality(double quality) {
        this.quality = quality;
    }

    public synchronized void setId(int id) {
        this.id = id;
    }

    public synchronized void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
        this.price = startPrice;
    }

    public synchronized void setSold(boolean sold) {
        this.sold = sold;
    }

    public synchronized void setPrice(BigDecimal price) {
        if (price.compareTo(this.price) > 0) {
            this.price = price;
        }
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized boolean isSold() {
        return sold;
    }

    public synchronized BigDecimal getPrice() {
        return price;
    }


    @Override
    public void run() {
        System.out.println("Lot processing started");
        Auction auction = Auction.getInstance();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
            auction.process(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Lot{" +
                "id=" + id +
                ", startPrice=" + startPrice +
                ", sold=" + sold +
                ", price=" + price +
                ", value=" + quality +
                '}';
    }
}
