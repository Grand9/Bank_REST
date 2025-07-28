package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferMapper {

    TransferResponse toDto(Transfer transfer);

    Transfer toEntity(TransferResponse transferResponse);
}