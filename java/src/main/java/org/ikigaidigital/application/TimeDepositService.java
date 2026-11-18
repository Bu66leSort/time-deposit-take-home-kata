package org.ikigaidigital.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ikigaidigital.application.port.in.GetTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.out.TimeDepositPort;
import org.ikigaidigital.domain.TimeDepositView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeDepositService implements UpdateBalancesUseCase, GetTimeDepositsUseCase {
    static final int CHUNK_SIZE = 500;
    static final int MAX_PAGE_SIZE = 100;

    private final AccrualChunkProcessor chunkProcessor;
    private final TimeDepositPort port;
    private final Clock clock;

    /*
     * Assumptions:
     * - The endpoint is meant to be triggered once a month. Interest is accrued at most once per calendar
     *   month per deposit (tracked by last_accrual_month), so repeated calls are idempotent. A deposit still
     *   inside the 30-day grace period is also marked for the month and gets no interest until the next run.
     * - balance is already net of withdrawals: they are history only, TimeDepositCalculator does not read them.
     * - Each chunk of 500 is its own transaction: a failure rolls back only that chunk; re-running the endpoint
     *   continues with the deposits that were not accrued yet.
     */
    @Override
    public int updateBalances() {
        LocalDate today = LocalDate.now(clock);
        LocalDate month = today.withDayOfMonth(1);

        int total = 0;
        int lastId = 0;
        AccrualChunkProcessor.ChunkResult result;
        do {
            result = chunkProcessor.process(month, today, lastId, CHUNK_SIZE);
            total += result.processed();
            lastId = result.lastId();
        } while (result.processed() == CHUNK_SIZE);

        log.info("Balances updated for {} time deposits (month {})", total, month);
        return total;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeDepositView> getTimeDeposits(int page, int size) {
        return port.findPage(page, Math.min(size, MAX_PAGE_SIZE), LocalDate.now(clock));
    }
}
