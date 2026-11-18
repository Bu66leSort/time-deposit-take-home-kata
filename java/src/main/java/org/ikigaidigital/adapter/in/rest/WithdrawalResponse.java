package org.ikigaidigital.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "Withdrawal")
public record WithdrawalResponse(Integer id, Integer timeDepositId, BigDecimal amount, LocalDate date) {
}
