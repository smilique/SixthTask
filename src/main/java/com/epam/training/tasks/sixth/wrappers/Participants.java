package com.epam.training.tasks.sixth.wrappers;

import com.epam.training.tasks.sixth.entities.Participant;

import java.util.List;

public class Participants {
    private List<Participant> participants;

    public Participants() {
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
