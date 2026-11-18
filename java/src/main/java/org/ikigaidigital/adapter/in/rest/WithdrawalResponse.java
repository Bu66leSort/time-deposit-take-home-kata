package org.ikigaidigital.adapter.in.rest;


import java.math.BigDecimal;
import java.time.LocalDate;

public record WithdrawalResponse(
        Integer id,
        BigDecimal amount,
        LocalDate date
) {
}
