package org.ikigaidigital.application.port.in;

public interface UpdateBalancesUseCase {
    /** @return number of deposits processed in this run */
    int updateBalances();
}
