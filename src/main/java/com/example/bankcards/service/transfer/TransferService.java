package com.example.bankcards.service.transfer;

import com.example.bankcards.dto.response.TransferResponse;

public interface TransferService {
    TransferResponse transfer(TransferResponse transferResponse);

    void cancelTransfer(TransferResponse transferResponse);


}