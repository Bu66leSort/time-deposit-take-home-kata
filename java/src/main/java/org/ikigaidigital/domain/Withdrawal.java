package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Withdrawal(Integer id, Integer timeDepositId, BigDecimal amount, LocalDate date) {
}
