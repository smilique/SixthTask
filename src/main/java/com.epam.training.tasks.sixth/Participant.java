package com.epam.training.tasks.sixth;

import java.math.BigDecimal;

public class Participant implements Runnable{
//private final static BigD


    private int id;
    private BigDecimal funds;

    public void process(Lot lot) {
        BigDecimal lotPrice = lot.getPrice();
        BigDecimal fundsPercent = funds.divide(lotPrice,3);
        if (fundsPercent.compareTo(new BigDecimal("0.7")) > 0) {
            System.out.println("Patricipant id:" + this.id + " ready to bet lot id: " + lot.getId());
        } else {
            System.out.println("Patricipant id:" + this.id + " is NOT ready to bet lot id: " + lot.getId());
        }
        //funds.divide(lotPrice,10)
   // if (condition is good) submit currentparticipant to
        //if (funds - lotPrice > 1)
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
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
        auction.process(this);
    }
}
