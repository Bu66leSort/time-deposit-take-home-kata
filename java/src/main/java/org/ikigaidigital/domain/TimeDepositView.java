package org.ikigaidigital.domain;

import java.math.BigDecimal;
import java.util.List;

/** Read model for listing: TimeDeposit itself cannot carry withdrawals and must stay unchanged. */
public record TimeDepositView(Integer id, String planType, BigDecimal balance, int days, List<Withdrawal> withdrawals) {
}
