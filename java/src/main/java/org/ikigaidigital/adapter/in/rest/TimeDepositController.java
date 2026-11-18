package org.ikigaidigital.adapter.in.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.in.UpdateBalancesUseCase;
import org.ikigaidigital.application.port.in.GetTimeDepositsUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/time-deposits")
@RequiredArgsConstructor
@Tag(name = "Time deposits")
public class TimeDepositController {
    private final UpdateBalancesUseCase updateBalancesUseCase;
    private final GetTimeDepositsUseCase getTimeDepositsUseCase;
    private final TimeDepositRestMapper mapper;

    @PatchMapping("/balances")
    @Operation(summary = "Accrue monthly interest on all time deposits",
            description = "Idempotent per calendar month: a repeated call in the same month returns 0.")
    public UpdateBalancesResponse updateBalances() {
        return new UpdateBalancesResponse(updateBalancesUseCase.updateBalances());
    }

    @GetMapping
    @Operation(summary = "List time deposits", description = "Ordered by id; page size is capped at 100.")
    public List<TimeDepositResponse> getTimeDeposits(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        return mapper.toResponses(getTimeDepositsUseCase.getTimeDeposits(page, size));
    }
}
