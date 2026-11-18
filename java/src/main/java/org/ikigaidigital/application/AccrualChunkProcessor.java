package org.ikigaidigital.application;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.out.TimeDepositPort;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositCalculator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Separate bean so that the transaction proxy applies to each chunk (no self-invocation). */
@Component
@RequiredArgsConstructor
public class AccrualChunkProcessor {
    private final TimeDepositPort port;
    private final TimeDepositCalculator calculator;

    public record ChunkResult(int processed, int lastId) {
    }

    @Transactional
    public ChunkResult process(LocalDate month, LocalDate today, int afterId, int chunkSize) {
        List<TimeDeposit> chunk = port.lockNotAccrued(month, today, afterId, chunkSize);
        if (chunk.isEmpty()) {
            return new ChunkResult(0, afterId);
        }
        calculator.updateBalance(chunk);
        port.saveAccrued(chunk, month);
        return new ChunkResult(chunk.size(), chunk.get(chunk.size() - 1).getId());
    }
}
