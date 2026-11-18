package org.ikigaidigital.domain;

import org.ikigaidigital.domain.interest.BasicInterestStrategy;
import org.ikigaidigital.domain.interest.InterestStrategy;
import org.ikigaidigital.domain.interest.PremiumInterestStrategy;
import org.ikigaidigital.domain.interest.StudentInterestStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeDepositCalculator {
    private static final int GRACE_PERIOD_DAYS = 30;

    private final Map<String, InterestStrategy> strategies;

    public TimeDepositCalculator() {
        this(List.of(new BasicInterestStrategy(), new StudentInterestStrategy(), new PremiumInterestStrategy()));
    }

    public TimeDepositCalculator(List<InterestStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(InterestStrategy::planType, Function.identity()));
    }

    public void updateBalance(List<TimeDeposit> xs) {
        for (TimeDeposit deposit : xs) {
            double interest = 0;

            if (deposit.getDays() > GRACE_PERIOD_DAYS) {
                // Assumption: an unknown plan type earns no interest (behaviour of the original code).
                InterestStrategy strategy = strategies.get(deposit.getPlanType());
                if (strategy != null) {
                    interest = strategy.monthlyInterest(deposit.getBalance(), deposit.getDays());
                }
            }

            double a2d = deposit.getBalance() + (new BigDecimal(interest).setScale(2, RoundingMode.HALF_UP)).doubleValue();
            deposit.setBalance(a2d);
        }
    }
}
