package org.ikigaidigital.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.out.TimeDepositPort;
import org.ikigaidigital.domain.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositView;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
class TimeDepositPersistenceAdapter implements TimeDepositPort {
    private final TimeDepositJpaRepository repository;
    private final TimeDepositEntityMapper mapper;

    @Override
    public List<TimeDeposit> lockNotAccrued(LocalDate month, LocalDate today, int afterId, int limit) {
        return repository.lockNotAccrued(month, afterId, PageRequest.ofSize(limit)).stream()
                .map(entity -> mapper.toDomain(entity, daysSince(entity, today)))
                .toList();
    }

    @Override
    public void saveAccrued(List<TimeDeposit> deposits, LocalDate month) {
        // Entities were loaded by lockNotAccrued in the same transaction, so getReferenceById
        // returns them from the persistence context; changes are flushed in batches on commit.
        for (TimeDeposit deposit : deposits) {
            mapper.updateEntity(deposit, month, repository.getReferenceById(deposit.getId()));
        }
    }

    @Override
    public List<TimeDepositView> findPage(int page, int size, LocalDate today) {
        return repository.findAllByOrderByIdAsc(PageRequest.of(page, size)).stream()
                .map(entity -> mapper.toView(entity, daysSince(entity, today)))
                .toList();
    }

    private static int daysSince(TimeDepositEntity entity, LocalDate today) {
        return (int) ChronoUnit.DAYS.between(entity.getCreatedOn(), today);
    }
}
