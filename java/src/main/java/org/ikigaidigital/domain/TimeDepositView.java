package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.util.List;

public record TimeDepositView(
        Integer id,
        String planType,
        BigDecimal balance,
        int days,
        List<Withdrawal> withdrawals
) {
}
