package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {

    private static volatile Auction instance;
    //thread-safe singleton
    public static Auction getInstance() {
        Auction localInstance = instance;
        if (localInstance == null) {
            synchronized (Auction.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Auction();
                }
            }
        }
        return localInstance;
    }

    private List<Participant> participants;


    public void process(Lot lot) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        Lock lock = new ReentrantLock();
        semaphore.acquire();
        lock.lock();
        try {
            BigDecimal priceBeforeBidding = lot.getPrice();
            participants.forEach(participant -> {
                participant.process(lot);
            });
            BigDecimal priceAfterBidding = lot.getPrice();
            if (priceBeforeBidding.compareTo(priceAfterBidding) == 0) {

            }
        } finally {
            semaphore.release();
            lock.unlock();
        }

    }

    public void setParticipants(List<Participant> participants){
        this.participants = participants;
    }

    public void process(Participant participant) {
        System.out.println("хз как тут поступить");
    }



}
