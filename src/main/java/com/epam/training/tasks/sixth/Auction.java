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
    private final Lock lock = new ReentrantLock();
    private final static double BASE_INCREMENT = 1.1;
    private final static double RESERVE_COEFFICIENT = 0.7;

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

    private Auction() {
    }

    public void process(Lot lot) throws InterruptedException {

        lotSemaphore.acquire();
        lock.lock();

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
            LOGGER.info("Nobody wants to buy lot id: " + currentLot.getId());
        }
        resetParticipants();
        lotSemaphore.release();
        lock.unlock();
    }

    private void buy() {
        Participant winner = participants.get(leaderId - 1);
        BigDecimal funds = winner.getFunds();
        BigDecimal price = currentLot.getPrice();
        BigDecimal newFunds = funds.subtract(price);
        winner.setFunds(newFunds);
        LOGGER.info(newFunds);
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



    public void process(Participant participant) throws InterruptedException {
        participantSemaphore.acquire();
        participantLock.lock();
        try {
            TimeUnit.MILLISECONDS.sleep(30);
            if ((participantsPresent() != 1) && (leaderId != participant.getId())) {
                int participantId = participant.getId();
                LOGGER.debug("Participant id: " + participantId);
                boolean buying = isBuying();
                boolean solvent = isParticipantSolvent(participant);
                boolean interested = participant.isInterested();
                if (buying && solvent && interested) {
                    LOGGER.debug("Wants to buy lot id: " + currentLot.getId() + " - " + buying);
                    LOGGER.debug("Participant id: " + participantId + " is solvent? - " + solvent);
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

    private void bid() throws InterruptedException {
        BigDecimal lastPrice = currentLot.getPrice();
        BigDecimal bidIncrement = new BigDecimal(BASE_INCREMENT + random.nextDouble() / 10);
        BigDecimal bidPrice = lastPrice.multiply(bidIncrement, mathContext);
        currentLot.setPrice(bidPrice);
        LOGGER.info("Participant rises price from " + lastPrice + " to " + bidPrice);
    }

    private boolean isBuying() {
        double will = random.nextDouble();
        return currentLot.getQuality() > will;
    }

    private boolean isParticipantSolvent(Participant participant) throws InterruptedException {
        BigDecimal funds = participant.getFunds();
        return funds.multiply(new BigDecimal(RESERVE_COEFFICIENT)).compareTo(currentLot.getPrice()) > 0;
    }

}
