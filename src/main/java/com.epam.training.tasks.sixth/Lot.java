package com.epam.training.tasks.sixth;

import java.math.BigDecimal;

public class Lot implements Runnable{

    private int id;
    private BigDecimal startPrice;
    private boolean sold;
    private BigDecimal price;

    public  Lot() {
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setStartPrice(BigDecimal startPrice) {
        this.startPrice = startPrice;
        this.price = startPrice;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void setPrice(BigDecimal price) {
        if (price.compareTo(this.price) > 0) {
            this.price = price;
        }
    }

    public int getId() {
        return id;
    }

    public boolean isSold() {
        return sold;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void run() {
        Auction auction = Auction.getInstance();
        try {
            auction.process(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Lot{" +
                "id=" + id +
                ", strartPrice=" + startPrice +
                ", sold=" + sold +
                '}';
    }
}
