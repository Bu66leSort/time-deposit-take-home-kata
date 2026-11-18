package org.ikigaidigital;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TimeDepositCalculatorTest {
    private final TimeDepositCalculator calc = new TimeDepositCalculator();

    private double balanceAfter(String planType, double balance, int days) {
        TimeDeposit deposit = new TimeDeposit(1, planType, balance, days);
        calc.updateBalance(List.of(deposit));
        return deposit.getBalance();
    }

    @Test
    public void noInterestInFirst30DaysForAnyPlan() {
        assertThat(balanceAfter("basic", 12000.0, 30)).isEqualTo(12000.0);
        assertThat(balanceAfter("student", 12000.0, 30)).isEqualTo(12000.0);
        assertThat(balanceAfter("premium", 12000.0, 30)).isEqualTo(12000.0);
    }

    @Test
    public void basicPaysOnePercentPerYearMonthlyFromDay31() {
        assertThat(balanceAfter("basic", 12000.0, 31)).isCloseTo(12010.0, within(0.001));
        assertThat(balanceAfter("basic", 12000.0, 1000)).isCloseTo(12010.0, within(0.001));
    }

    @Test
    public void studentPaysThreePercentUpTo365DaysOnly() {
        assertThat(balanceAfter("student", 12000.0, 31)).isCloseTo(12030.0, within(0.001));
        assertThat(balanceAfter("student", 12000.0, 365)).isCloseTo(12030.0, within(0.001));
        assertThat(balanceAfter("student", 12000.0, 366)).isEqualTo(12000.0);
    }

    @Test
    public void premiumPaysFivePercentOnlyAfter45Days() {
        assertThat(balanceAfter("premium", 12000.0, 45)).isEqualTo(12000.0);
        assertThat(balanceAfter("premium", 12000.0, 46)).isCloseTo(12050.0, within(0.001));
    }

    @Test
    public void interestIsRoundedToTwoDecimalsHalfUp() {
        // 1234567 * 0.01 / 12 = 1028.8058... -> 1028.81
        assertThat(balanceAfter("basic", 1234567.00, 45)).isCloseTo(1235595.81, within(0.001));
        // 1000.50 * 0.01 / 12 = 0.83375 -> 0.83
        assertThat(balanceAfter("basic", 1000.50, 45)).isCloseTo(1001.33, within(0.001));
    }

    @Test
    public void unknownPlanGetsNoInterest() {
        assertThat(balanceAfter("gold", 12000.0, 100)).isEqualTo(12000.0);
    }

    @Test
    public void updatesEveryDepositInTheList() {
        TimeDeposit basic = new TimeDeposit(1, "basic", 12000.0, 31);
        TimeDeposit premium = new TimeDeposit(2, "premium", 12000.0, 46);

        calc.updateBalance(List.of(basic, premium));

        assertThat(basic.getBalance()).isCloseTo(12010.0, within(0.001));
        assertThat(premium.getBalance()).isCloseTo(12050.0, within(0.001));
    }
}
