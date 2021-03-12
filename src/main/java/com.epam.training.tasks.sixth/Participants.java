package com.epam.training.tasks.sixth;

import java.util.List;

public class Participants {
    private List<Participant> participants;

    public Participants(){
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return "Participants{" +
                "participants=" + participants +
                '}';
    }
}
