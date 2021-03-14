package com.epam.training.tasks.sixth.entities;

import com.epam.training.tasks.sixth.Auction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

public class Participant implements Runnable {

    private final static Logger LOGGER = Logger.getLogger(Participant.class);

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

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public BigDecimal getFunds() {
        return this.funds;
    }

    @Override
    public void run() {
        Auction auction = Auction.getInstance();
        try {
            auction.process(this);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(),e);
        }

    }

    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", funds=" + funds +
                '}';
    }


}
