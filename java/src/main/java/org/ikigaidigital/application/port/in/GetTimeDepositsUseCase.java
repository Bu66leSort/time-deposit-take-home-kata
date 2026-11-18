package org.ikigaidigital.application.port.in;

import org.ikigaidigital.domain.TimeDepositView;

import java.util.List;

public interface GetTimeDepositsUseCase {
    List<TimeDepositView> getTimeDeposits(int page, int size);
}
