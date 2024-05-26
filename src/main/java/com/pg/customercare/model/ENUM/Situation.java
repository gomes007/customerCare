package com.pg.customercare.model.ENUM;

public enum Situation {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String label;

    Situation(String label) {
        this.label = label;
    }

    public String getLabel(){return this.label;}
}
