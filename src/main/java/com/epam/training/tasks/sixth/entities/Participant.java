package com.epam.training.tasks.sixth.entities;

import com.epam.training.tasks.sixth.Auction;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;


public class Participant implements Runnable {

    private int id;
    private BigDecimal funds;
    private boolean interested = true;

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }



    public BigDecimal getFunds() {
            return this.funds;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", funds=" + funds +
                '}';
    }

    @Override
    public void run() {
        Auction auction = Auction.getInstance();
        try {
            TimeUnit.MILLISECONDS.sleep(10);
            auction.process(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
