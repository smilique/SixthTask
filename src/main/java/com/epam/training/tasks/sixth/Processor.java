package com.epam.training.tasks.sixth;

import com.epam.training.tasks.sixth.entities.Lot;
import com.epam.training.tasks.sixth.entities.Participant;
import com.epam.training.tasks.sixth.wrappers.Lots;
import com.epam.training.tasks.sixth.wrappers.Participants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Processor {
    private static final String LOT_INPUT = "./src/main/resources/lots.json";
    private static final String PARTICIPANTS_INPUT = "./src/main/resources/participants.json";
    private static final Logger LOGGER = Logger.getLogger(Processor.class);

    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        Lots lotWrapper = mapper.readValue(new File(LOT_INPUT), Lots.class);
        List<Lot> lotList = lotWrapper.getLots();
        LOGGER.debug("Today's auction lots: " + lotList);

        Participants participantsWrapper = mapper.readValue(new File(PARTICIPANTS_INPUT), Participants.class);
        List<Participant> participantList = participantsWrapper.getParticipants();
        LOGGER.debug("Auction participants: " + participantList);

        Auction auction = Auction.getInstance();
        auction.setParticipants(participantList);

        List<Future<?>> lotFutures = new ArrayList<>();
        ExecutorService lotExecutor = Executors.newSingleThreadExecutor();

        lotList.forEach(lot -> {
            Future<?> lotFuture = lotExecutor.submit(lot);
            lotFutures.add(lotFuture);
        });
        lotExecutor.shutdown();

        lotFutures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(e.getMessage(),e);
            }
        });

    }
}
