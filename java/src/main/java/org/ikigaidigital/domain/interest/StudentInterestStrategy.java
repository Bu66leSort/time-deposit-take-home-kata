package org.ikigaidigital.domain.interest;

public class StudentInterestStrategy implements InterestStrategy {
    @Override
    public String planType() {
        return "student";
    }

    @Override
    public double monthlyInterest(double balance, int days) {
        // No interest after one year.
        return days < 366 ? balance * 0.03 / 12 : 0;
    }
}
