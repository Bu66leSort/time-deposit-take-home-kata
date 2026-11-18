package org.ikigaidigital.application.port.out;

import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositView;

import java.time.LocalDate;
import java.util.List;

public interface TimeDepositPort {
    /**
     * Locks and returns up to {@code limit} deposits with id greater than {@code afterId} that were
     * not accrued for {@code month} yet, ordered by id. {@code days} is computed relative to {@code today}.
     */
    List<TimeDeposit> lockNotAccrued(LocalDate month, LocalDate today, int afterId, int limit);

    /** Persists balance and days of the given (previously locked) deposits and marks them accrued for {@code month}. */
    void saveAccrued(List<TimeDeposit> deposits, LocalDate month);

    List<TimeDepositView> findPage(int page, int size, LocalDate today);
}
