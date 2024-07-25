package com.pg.customercare.model.ENUM;

public enum Situation {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String label;

    Situation(String label) {
        this.label = label;
    }

    public String getLabel(){return this.label;}
}
