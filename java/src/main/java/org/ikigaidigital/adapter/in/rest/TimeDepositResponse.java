package org.ikigaidigital.adapter.in.rest;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "TimeDeposit")
public record TimeDepositResponse(Integer id, String planType, BigDecimal balance, int days,
                                  List<WithdrawalResponse> withdrawals) {
}
