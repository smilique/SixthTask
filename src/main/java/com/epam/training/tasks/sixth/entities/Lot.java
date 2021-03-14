package com.epam.training.tasks.sixth.entities;

import com.epam.training.tasks.sixth.Auction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class Lot implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Participant.class);

    private int id;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private double quality;

    public Lot() {
    }

    public double getQuality() {
        return quality;
    }

    public BigDecimal getStartPrice() {
        return startPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.currentPrice = newPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    @Override
    public void run() {
        System.out.println("Lot id: " + id + " auction started!");
        Auction auction = Auction.getInstance();
        setCurrentPrice(startPrice);
        try {
            auction.process(this);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    @Override
    public String toString() {
        return "Lot{" +
                "id=" + id +
                ", startPrice=" + startPrice +
                ", price=" + currentPrice +
                ", value=" + quality +
                '}';
    }
}
