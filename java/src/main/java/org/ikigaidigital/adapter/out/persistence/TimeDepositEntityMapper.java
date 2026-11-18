package org.ikigaidigital.adapter.out.persistence;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.domain.TimeDepositView;
import org.ikigaidigital.domain.Withdrawal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
interface TimeDepositEntityMapper {

    // days is derived from created_on, not taken from the stored column.
    @Mapping(target = "days", source = "currentDays")
    TimeDeposit toDomain(TimeDepositEntity entity, int currentDays);

    @Mapping(target = "days", source = "currentDays")
    TimeDepositView toView(TimeDepositEntity entity, int currentDays);

    @Mapping(target = "timeDepositId", source = "timeDeposit.id")
    Withdrawal toWithdrawal(WithdrawalEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "withdrawals", ignore = true)
    void updateEntity(TimeDeposit deposit, LocalDate lastAccrualMonth, @MappingTarget TimeDepositEntity entity);

    // Calculator works in double; fix the scale here so rounding does not depend on the column type.
    default BigDecimal toMoney(Double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}
