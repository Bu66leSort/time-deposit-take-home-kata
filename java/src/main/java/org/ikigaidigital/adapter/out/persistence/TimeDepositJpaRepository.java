package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {

    // Keyset pagination by id; the limit comes from the Pageable. Concurrent runs wait on row locks
    // and then skip rows already accrued by the other run (re-checked by the filter after the lock).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select d from TimeDepositEntity d
            where d.id > :afterId
              and (d.lastAccrualMonth is null or d.lastAccrualMonth < :month)
            order by d.id
            """)
    List<TimeDepositEntity> lockNotAccrued(@Param("month") LocalDate month,
                                           @Param("afterId") int afterId,
                                           Pageable limit);

    List<TimeDepositEntity> findAllByOrderByIdAsc(Pageable pageable);
}
