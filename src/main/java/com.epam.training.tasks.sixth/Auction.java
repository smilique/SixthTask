package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Auction {

    private final static AtomicReference<Auction> instance = new AtomicReference<>();

    private final Random random = new Random();
    private final List<Participant> notInterested = new ArrayList<>();
    private List<Participant> participants;


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

    public void removeBuyer(Participant participant) throws InterruptedException {
        int id = participant.getId();
        System.out.println("Removing participant id:" + id);
        notInterested.add(participant);
    }
        public List<Participant> getNotInterested() {
        return notInterested;
    }

    public int totalBuyers() {
        return participants.size()-notInterested.size();
    }
    private Lot currentLot;

    public void process(Lot lot) throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        Lock lock = new ReentrantLock();
        semaphore.acquire();
        lock.lock();

        notInterested.clear();
        currentLot = lot;

        ExecutorService executor = Executors.newFixedThreadPool(participants.size());
        List<Future<?>> futures = new ArrayList<>();
        participants.forEach(participant -> {
            Future<?> future = executor.submit(participant);
            futures.add(future);
        });
        executor.shutdown();

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

//    public void setLots(List<Lot> lots) {
//        this.lots = lots;
//    }

    public void setParticipants(List<Participant> participants){
        this.participants = participants;
    }

    public void process(Participant participant) throws InterruptedException {


        BigDecimal funds = participant.getFunds();

        do {
            Semaphore semaphore = new Semaphore(1);
            semaphore.acquire();
            Lock lock = new ReentrantLock();
            lock.lock();

            try {
                List<Participant> notInterested = getNotInterested();
                BigDecimal lotPrice = currentLot.getPrice();
                BigDecimal fundsPercent = funds.divide(lotPrice,3);
                double lotValue = currentLot.getQuality();
                double wantToBuy = random.nextDouble();
                double buyingCriteria = lotValue * wantToBuy;
                System.out.println(buyingCriteria);
                if ((buyingCriteria > 0.3) & (!notInterested.contains(participant))){
                    if (fundsPercent.compareTo(new BigDecimal("0.7")) > 0) {
                        System.out.println("Patricipant id:" + participant.getId() + " ready to bet lot id: " + currentLot.getId());
                    } else {
                        System.out.println("Not interested buyers: " + notInterested.size());
                        System.out.println("Patricipant id:" + participant.getId() + " is NOT ready to bet lot id: " + currentLot.getId());
                        removeBuyer(participant);
                        System.out.println("Buyers after removal: " + totalBuyers());
                    }
                }
            } finally {
                semaphore.release();
                lock.unlock();
            }
        } while (totalBuyers() != 1);

    }



}
