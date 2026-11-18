package org.ikigaidigital.domain.interest;

public class PremiumInterestStrategy implements InterestStrategy {
    @Override
    public String planType() {
        return "premium";
    }

    @Override
    public double monthlyInterest(double balance, int days) {
        return days > 45 ? balance * 0.05 / 12 : 0;
    }
}
