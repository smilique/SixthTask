package com.epam.training.tasks.sixth;

import com.epam.training.tasks.sixth.entities.Lot;
import com.epam.training.tasks.sixth.entities.Participant;
import org.apache.log4j.Logger;

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
    private final static Logger LOGGER = Logger.getLogger(Auction.class);
    private final static AtomicReference<Auction> INSTANCE = new AtomicReference<>();

    private List<Participant> participants;
    private Lot currentLot;
    private int leaderId;

    private final Random random = new Random();
    private final MathContext mathContext = new MathContext(8);
    private final Semaphore participantSemaphore = new Semaphore(1);
    private final Lock participantLock = new ReentrantLock();

    private final Semaphore lotSemaphore = new Semaphore(1);
    private final Lock lotLock = new ReentrantLock();
    private final static double BASE_INCREMENT = 0.1;
    private final static double RESERVE_COEFFICIENT = 0.7;
    private final static int TIMEOUT = 500;

    private Auction() {
    }

    public static Auction getInstance() {
        Auction localInstance = INSTANCE.get();
        if (localInstance == null) {
            localInstance = INSTANCE.get();
            if (localInstance == null) {
                localInstance = new Auction();
                INSTANCE.set(localInstance);
            }

        }
        return localInstance;
    }

    public void process(Lot lot) throws InterruptedException {
        lotSemaphore.acquire();
        lotLock.lock();

        currentLot = lot;
        int totalParticipants = participantsPresent();

        while (totalParticipants > 1) {
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
            totalParticipants = participantsPresent();
        }
        if (totalParticipants == 1) {
            LOGGER.info("The winner is id: " + leaderId);
            buy();
        } else {
            System.out.println("Lot " + lot.getId() + " is off the auction!");
            LOGGER.info("Nobody wants to buy lot id: " + currentLot.getId());
        }

        resetParticipants();
        lotSemaphore.release();
        lotLock.unlock();
    }

    public void process(Participant participant) throws InterruptedException {
        participantSemaphore.acquire();
        participantLock.lock();

        try {
            TimeUnit.MILLISECONDS.sleep(TIMEOUT);
            if ((participantsPresent() != 1) && (leaderId != participant.getId())) {
                int participantId = participant.getId();

                LOGGER.debug("Participant id: " + participantId);

                if (isBuying() && isSolvent(participant) && isInterested(participant)) {
                    LOGGER.debug("Wants to buy lot id: " + currentLot.getId());
                    LOGGER.debug("Participant id: " + participantId + " is solvent");
                    System.out.print("Participant id: " + participantId);

                    bid();
                    leaderId = participantId;
                } else {
                    LOGGER.debug("Participant id: " + participantId + " won't buy lot id: " + currentLot.getId());

                    participant.setInterested(false);
                }
            }
        } finally {
            participantSemaphore.release();
            participantLock.unlock();
        }
    }

    private void buy() {
        Participant winner = participants.get(leaderId - 1);
        BigDecimal funds = winner.getFunds();
        BigDecimal price = currentLot.getCurrentPrice();
        BigDecimal newFunds = funds.subtract(price);
        winner.setFunds(newFunds);

        System.out.println("Participant id: " + leaderId
                + " won lot id: " + currentLot.getId()
                + " with price: " + price
                + " | rest of funds: " + newFunds + "\n");
    }

    private int participantsPresent() {
        int participantsCount = (int) participants.stream()
                .filter(Participant::isInterested)
                .count();

        return participantsCount;
    }

    public void resetParticipants() {
        participants.forEach(participant -> {
            participant.setInterested(true);
            LOGGER.info(participant);
        });
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    private void bid() {
        BigDecimal startPrice = currentLot.getStartPrice();
        BigDecimal lastPrice = currentLot.getCurrentPrice();
        BigDecimal bidIncrement = new BigDecimal(BASE_INCREMENT + random.nextDouble() / 10);
        BigDecimal incrementValue = startPrice.multiply(bidIncrement, mathContext);
        BigDecimal bidPrice = lastPrice.add(incrementValue, mathContext);
        currentLot.setNewPrice(bidPrice);

        LOGGER.debug("Price changed " + lastPrice + " to " + bidPrice);
        System.out.println(" rises price from " + lastPrice + " to " + bidPrice);
    }

    private boolean isBuying() {
        double will = random.nextDouble();
        return currentLot.getQuality() > will;
    }

    private boolean isSolvent(Participant participant) {
        BigDecimal funds = participant.getFunds();
        BigDecimal currentPrice = currentLot.getCurrentPrice();
        BigDecimal reserve = funds.multiply(new BigDecimal(RESERVE_COEFFICIENT));

        LOGGER.debug("Reserve value: " + reserve);

        return reserve.compareTo(currentPrice) > 0;
    }

    private boolean isInterested(Participant participant) {
        return participant.isInterested();
    }

}
