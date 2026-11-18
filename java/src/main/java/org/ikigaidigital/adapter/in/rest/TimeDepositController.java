package org.ikigaidigital.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.in.GetTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/time-deposits")
@RequiredArgsConstructor
@Tag(name = "Time deposits")
public class TimeDepositController {
    private final UpdateBalancesUseCase updateBalancesUseCase;
    private final GetTimeDepositsUseCase getTimeDepositsUseCase;
    private final TimeDepositRestMapper mapper;

    @PatchMapping("/balances")
    @Operation(summary = "Accrue monthly interest on all time deposits",
            description = """
                    Updates the balance of every time deposit that was not accrued in the current calendar month yet.
                    Idempotent per month: calling it again in the same month changes nothing and returns 0.
                    Assumption: balance is already net of withdrawals; withdrawals are history only and do not
                    affect the interest. Returns the number of deposits processed.""")
    public UpdateBalancesResponse updateBalances() {
        return new UpdateBalancesResponse(updateBalancesUseCase.updateBalances());
    }

    @GetMapping
    @Operation(summary = "List time deposits",
            description = """
                    Returns time deposits ordered by id, with their withdrawals. 'days' is derived from the
                    creation date. Assumption: balance is already net of withdrawals (they are shown as history).
                    Page size is capped at 100.""")
    public List<TimeDepositResponse> getTimeDeposits(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return mapper.toResponses(getTimeDepositsUseCase.getTimeDeposits(page, size));
    }
}
