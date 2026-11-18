package org.ikigaidigital.domain.interest;

public interface InterestStrategy {
    String planType();

    /**
     * Monthly interest for a deposit that already passed the common grace period
     * (see TimeDepositCalculator). Plan-specific eligibility rules live here.
     */
    double monthlyInterest(double balance, int days);
}
