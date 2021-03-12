package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {

    //thread-safe singleton
    private final static AtomicReference<Auction> instance = new AtomicReference<>();

    public static Auction getInstance() {
        Auction localInstance = instance.get();
        if (localInstance == null) {
            synchronized (Auction.class) {
                localInstance = instance.get();
                if (localInstance == null) {
                    localInstance = new Auction();
                    instance.set(localInstance);
                }
            }
        }
        return localInstance;
    }

    private Auction(){

    }

    private List<Participant> participants;
    private Map<Integer,Participant> buyers;

    public void addBuyer(Participant participant) {
        int id = participant.getId();
        buyers.put(id,participant);
    }

    public void removeBuyer(Participant participant) {
        int id = participant.getId();
        buyers.remove(id);
    }

    public int totalBuyers() {
        return buyers.size();
    }


    public void process(Lot lot) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        Lock lock = new ReentrantLock();
        semaphore.acquire();
        lock.lock();
        //lock.tryLock(50L, TimeUnit.MILLISECONDS);

        try {
            BigDecimal priceBeforeBidding = lot.getPrice();
            //for (Participant participant1 : participants) {
            //    addBuyer(participant1);
            //}

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
