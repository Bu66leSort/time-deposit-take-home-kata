package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time_deposits")
@Getter
@Setter
public class TimeDepositEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "plan_type", nullable = false)
    private String planType;

    private int days;

    private BigDecimal balance;

    @Column(name = "created_on", nullable = false)
    private LocalDate createdOn;

    @Column(name = "last_accrual_month")
    private LocalDate lastAccrualMonth;

    // @BatchSize loads withdrawals of a whole page in one query instead of N+1.
    @OneToMany(mappedBy = "timeDeposit", fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    private List<WithdrawalEntity> withdrawals = new ArrayList<>();
}
