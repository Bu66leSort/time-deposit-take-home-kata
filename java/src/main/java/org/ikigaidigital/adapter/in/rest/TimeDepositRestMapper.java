package org.ikigaidigital.adapter.in.rest;

import org.ikigaidigital.domain.TimeDepositView;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
interface TimeDepositRestMapper {
    TimeDepositResponse toResponse(TimeDepositView view);

    List<TimeDepositResponse> toResponses(List<TimeDepositView> views);
}
