package org.ikigaidigital.adapter.in.rest;


import java.math.BigDecimal;
import java.util.List;

public record TimeDepositResponse(
        Integer id,
        String planType,
        BigDecimal balance,
        int days,
        List<WithdrawalResponse> withdrawals
) {
}
