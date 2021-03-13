package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Participant implements Runnable{

    private int id;
    private BigDecimal funds;
    private final Random random = new Random();


    public void process(Lot lot) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        semaphore.acquire();
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
        Auction auction = Auction.getInstance();
        List<Participant> notInterested = auction.getNotInterested();
            BigDecimal lotPrice = lot.getPrice();
            BigDecimal fundsPercent = funds.divide(lotPrice,3);
            double lotValue = lot.getQuality();
            double wantToBuy = random.nextDouble();
            double buyingCriteria = lotValue * wantToBuy;
            if ((buyingCriteria > 0) & (!notInterested.contains(this))){
                if (fundsPercent.compareTo(new BigDecimal("0.7")) > 0) {
                    System.out.println("Patricipant id:" + this.id + " ready to bet lot id: " + lot.getId());
                } else {
                    System.out.println("Not interested buyers: " + notInterested.size());
                    System.out.println("Patricipant id:" + this.id + " is NOT ready to bet lot id: " + lot.getId());
                    auction.removeBuyer(this);
                    System.out.println("Buyers after removal: " + auction.totalBuyers());
                }
            }
        } finally {
            semaphore.release();
            lock.unlock();
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
