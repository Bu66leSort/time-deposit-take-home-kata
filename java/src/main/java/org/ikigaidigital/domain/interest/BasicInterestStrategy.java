package org.ikigaidigital.domain.interest;

public class BasicInterestStrategy implements InterestStrategy {
    @Override
    public String planType() {
        return "basic";
    }

    @Override
    public double monthlyInterest(double balance, int days) {
        return balance * 0.01 / 12;
    }
}
