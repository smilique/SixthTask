package com.epam.training.tasks.sixth.wrappers;

import com.epam.training.tasks.sixth.entities.Lot;

import java.util.List;

public class Lots {

    private List<Lot> lots;

    public Lots() {
    }

    public List<Lot> getLots() {
        return lots;
    }

    @Override
    public String toString() {
        return "Lots{" +
                "lots=" + lots +
                '}';
    }
}
