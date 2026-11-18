package org.ikigaidigital.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.in.GetTimeDepositsUseCase;
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

    // Assumption: interest is accrued once per calendar month; deposits in the grace period are marked too.
    // Assumption: balance is already net of withdrawals; they are history only.
    @Override
    public int updateBalances() {
        LocalDate today = LocalDate.now(clock);
        LocalDate month = today.withDayOfMonth(1);

        int total = 0;
        int lastId = 0;
        AccrualChunkProcessor.ChunkResult result;
        // Loop until an empty chunk, not a short one: FOR UPDATE + LIMIT can return fewer rows
        // when a concurrent run has just accrued some of the locked ones.
        do {
            result = chunkProcessor.process(month, today, lastId, CHUNK_SIZE);
            total += result.processed();
            lastId = result.lastId();
        } while (result.processed() > 0);

        log.info("Balances updated for {} time deposits (month {})", total, month);
        return total;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeDepositView> getTimeDeposits(int page, int size) {
        return port.findPage(page, Math.min(size, MAX_PAGE_SIZE), LocalDate.now(clock));
    }
}
