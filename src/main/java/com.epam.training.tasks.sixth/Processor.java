package com.epam.training.tasks.sixth;

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

    public static void main(String[] args) throws IOException{

        ObjectMapper mapper = new ObjectMapper();

        Lots lotWrapper = mapper.readValue(new File(LOT_INPUT), Lots.class);
        List<Lot> lotList = lotWrapper.getLots();
        System.out.println("Today's auction lots: " + lotList);

        Participants participantsWrapper = mapper.readValue(new File(PARTICIPANTS_INPUT), Participants.class);
        List<Participant> participantList = participantsWrapper.getParticipants();
        System.out.println("Auction participants: " + participantList);

        Auction auction = Auction.getInstance();
        auction.setParticipants(participantList);

        List<Future<?>> futures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(lotList.size());

        lotList.forEach(lot -> {
            Future<?> future = executorService.submit(lot);
            futures.add(future);
        });

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        lotList.forEach(System.out::println);
        executorService.shutdown();;

//        participantList.forEach(participant -> {
//           // futures.add
//            executorService.submit(participant);
//        });


//        ExecutorService executorService = Executors.newFixedThreadPool(lotList.size());
//        lotList.forEach(lot -> {
//            executorService.submit(lot);
//        });

    }
}
