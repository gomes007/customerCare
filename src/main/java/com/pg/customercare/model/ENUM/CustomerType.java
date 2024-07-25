package com.pg.customercare.model.ENUM;

public enum CustomerType {
  INDIVIDUAL("Individual"),
  CORPORATE("Corporate");

  private final String label;

  CustomerType(String label) {
    this.label = label;
  }

  public String getLabel() {
    return this.label;
  }
}
