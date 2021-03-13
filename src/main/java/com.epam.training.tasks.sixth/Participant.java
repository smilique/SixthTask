package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Participant implements Runnable {

    private int id;
    private BigDecimal funds;

    Semaphore semaphore = new Semaphore(1);

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFunds(BigDecimal funds) throws InterruptedException {
        semaphore.acquire();
        this.funds = funds;
        semaphore.release();
    }

    Semaphore fundSemaphore = new Semaphore(1);

    public BigDecimal getFunds() throws InterruptedException {
        fundSemaphore.acquire();
        try {
            return this.funds;
        } finally {
            fundSemaphore.release();
        }

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
