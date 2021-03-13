package com.epam.training.tasks.sixth;

import java.math.BigDecimal;
import java.math.MathContext;
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
    private Participant auctionLeader;

    private Semaphore participantSemaphore = new Semaphore(1);
    private Lock participantLock = new ReentrantLock();

    private final static double BASE_INCREMENT = 1.1;

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

    private Auction() {
    }

    private Semaphore removalSemaphore = new Semaphore(1);

    public void removeBuyer(Participant participant) throws InterruptedException {
        removalSemaphore.acquire();
        int id = participant.getId();
        if (totalBuyers() > 1) {
            System.out.println("Removing participant id:" + id);
            notInterested.add(participant);
        }
        removalSemaphore.release();
    }
//        public List<Participant> getNotInterested() {
//        return notInterested;
//    }

    private Semaphore buyersSemaphore = new Semaphore(1);

    public int totalBuyers() throws InterruptedException {
        buyersSemaphore.acquire();
        int result = participants.size() - notInterested.size();
        System.out.println("current active buyers: " + result);
        buyersSemaphore.release();

        return participants.size() - notInterested.size();
    }

    private Lot currentLot;
    private Semaphore lotSemaphore = new Semaphore(1);
    Lock lock = new ReentrantLock();

    public void process(Lot lot) throws InterruptedException {

        lotSemaphore.acquire();
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
        lotSemaphore.release();
        lock.unlock();
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    private int leaderId;

    public synchronized void process(Participant participant) throws InterruptedException {
        participantSemaphore.acquire();
        participantLock.lock();

        MathContext mathContext = new MathContext(8);
        BigDecimal funds = participant.getFunds();
        BigDecimal lotPrice = currentLot.getPrice();

        while (totalBuyers() > 1) {
            TimeUnit.MILLISECONDS.sleep(100);

            System.out.println(currentLot.getLeader());
            if (currentLot.getLeader() != participant.getId()){
                //System.out.println("Hello!");
                BigDecimal fundsPercent = funds.divide(lotPrice, 3);
                double lotValue = currentLot.getQuality();
                double wantToBuy = random.nextDouble();
                double buyingCriteria = lotValue * wantToBuy;

                if ((buyingCriteria > 0.1)) {
                    if ((fundsPercent.compareTo(new BigDecimal(0.7)) > 0) && (!notInterested.contains(participant))) {
                        currentLot.setLeader(participant);

                        BigDecimal bid = new BigDecimal(BASE_INCREMENT + random.nextDouble() / 10);
                        BigDecimal newLotPrice = lotPrice.multiply(bid,mathContext);
                        currentLot.setPrice(newLotPrice);
                        System.out.println("Patricipant id:" + participant.getId()
                                + " bets lot id: " + currentLot.getId()
                                + " with new  price: " + newLotPrice);
                    } else {
                        removeBuyer(participant);
                        System.out.println("Patricipant id:" + participant.getId()
                                + " left lot id: " + currentLot.getId()
                                + " auction - insufficient funds");
                    }
                } else {
                    removeBuyer(participant);
                    System.out.println("Patricipant id:" + participant.getId() + " left lot id: " + currentLot.getId() + " auction - not interested");
                }
            }

        }
        TimeUnit.MILLISECONDS.sleep(10);
        if ((auctionLeader != null) & (totalBuyers() == 1)) {
            BigDecimal fundsBefore = participant.getFunds();
            BigDecimal fundsAfter = fundsBefore.subtract(lotPrice,mathContext);
            System.out.println(fundsAfter);
            participant.setFunds(fundsAfter);

            System.out.println("Participant id: " + participant.getId() + " won lot id: " + currentLot.getId() + " with price: " + currentLot.getPrice());
        }
        participantSemaphore.release();
        participantLock.unlock();

    }


}
