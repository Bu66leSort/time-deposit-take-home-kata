package org.ikigaidigital.domain;

public class TimeDeposit {
    private int id;
    private String planType;
    // Assumption: Double is kept because the shared class must not be changed in a breaking way;
    // the persistence layer stores BigDecimal and converts at the boundary.
    private Double balance;
    private int days;

    public TimeDeposit(int id, String planType, Double balance, int days) {
        this.id = id;
        this.planType = planType;
        this.balance = balance;
        this.days = days;
    }

    public int getId() { return id; }

    public String getPlanType() {
        return planType;
    }

    public Double getBalance() {
        return balance;
    }

    public int getDays() {
        return days;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
