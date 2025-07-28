package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Transfer;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-28T17:03:03+0500",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.7 (Ubuntu)"
)
@Component
public class TransferMapperImpl implements TransferMapper {

    @Override
    public TransferResponse toDto(Transfer transfer) {
        if ( transfer == null ) {
            return null;
        }

        TransferResponse.TransferResponseBuilder transferResponse = TransferResponse.builder();

        transferResponse.id( transfer.getId() );
        transferResponse.amount( transfer.getAmount() );
        transferResponse.createdAt( transfer.getCreatedAt() );
        transferResponse.updatedAt( transfer.getUpdatedAt() );
        transferResponse.status( transfer.getStatus() );

        return transferResponse.build();
    }

    @Override
    public Transfer toEntity(TransferResponse transferResponse) {
        if ( transferResponse == null ) {
            return null;
        }

        Transfer.TransferBuilder transfer = Transfer.builder();

        if ( transferResponse.getId() != null ) {
            transfer.id( transferResponse.getId() );
        }
        transfer.amount( transferResponse.getAmount() );
        transfer.status( transferResponse.getStatus() );
        transfer.createdAt( transferResponse.getCreatedAt() );
        transfer.updatedAt( transferResponse.getUpdatedAt() );

        return transfer.build();
    }
}
