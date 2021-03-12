package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.Random;

public class Participant implements Runnable{

    private int id;
    private BigDecimal funds;
    private final Random random = new Random();

    public void process(Lot lot) {
        Auction auction = Auction.getInstance();
        BigDecimal lotPrice = lot.getPrice();
        BigDecimal fundsPercent = funds.divide(lotPrice,3);
        double lotValue = lot.getQuality();
        double wantToBuy = random.nextDouble();
        double buyingCriteria = lotValue * wantToBuy;
        if (buyingCriteria > 0) {
            if (fundsPercent.compareTo(new BigDecimal("0.7")) > 0) {
                System.out.println("Patricipant id:" + this.id + " ready to bet lot id: " + lot.getId());
            } else {
                System.out.println("Buyers before removal: " + auction.totalBuyers());
                System.out.println("Patricipant id:" + this.id + " is NOT ready to bet lot id: " + lot.getId());
                auction.removeBuyer(this);
                System.out.println("Buyers after removal: " + auction.totalBuyers());
            }
        }

        //funds.divide(lotPrice,10)
   // if (condition is good) submit currentparticipant to
        //if (funds - lotPrice > 1)
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
