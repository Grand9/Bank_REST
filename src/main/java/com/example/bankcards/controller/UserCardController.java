package com.example.bankcards.controller;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.BalanceResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.card.CardService;
import com.example.bankcards.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("v1/api/user/cards")
@PreAuthorize("hasRole('USER')")
public class UserCardController {
    private final CardService cardService;
    private final TransferService transferService;

    @Operation(
            summary = "Created a new card",
            description = "Creates a new card for the user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Card creation request payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardTypeRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Card successfully created"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/create/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(@PathVariable("userId")Long userId,
                                   @RequestBody @Valid CardTypeRequest cardTypeRequest) {
        return cardService.create(userId, cardTypeRequest);
    }
    @Operation(
            summary = "Set card status",
            description = "Changes the status of a card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card status successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PostMapping("/{id}/status/update")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse setCardStatus(@PathVariable("id") Long id,
                                      @RequestBody @Valid CardStatus cardStatus) {
        return cardService.changeStatus(id, cardStatus);
    }

    @Operation(
            summary = "Delete card",
            description = "Deletes a card by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCard(@PathVariable("id") Long id) {
        cardService.deleteCard(id);
    }

    @Operation(
            summary = "Find card by ID",
            description = "Retrieves details of a card by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card found",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @GetMapping("/get/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse findCardById(@PathVariable("cardId") Long cardId){
        return cardService.findCardById(cardId);
    }

    @Operation(
            summary = "Block card by ID",
            description = "Blocked card by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card blocked"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PutMapping("/request-block/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse blockCard(@PathVariable("cardId") Long cardId){
        return cardService.blockCard(cardId);
    }

    @Operation(
            summary = "Get balance a card",
            description = "Balance a card for the user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card blocked"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @GetMapping("/{cardId}/balance")
    public BalanceResponse getBalance(@PathVariable("cardId") Long cardId){
        return cardService.getBalance(cardId);
    }

    @Operation(
            summary = "Create transfer",
            description = "Create transfer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer successful",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse createTransfer(@RequestBody @Valid TransferResponse transferResponse) {
        return transferService.transfer(transferResponse);
    }

    @Operation(
            summary = "Get cards with filters",
            description = "Returns a paginated list of cards filtered by various criteria",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Search request with filter conditions",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardFilter.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Filtered card list returned successfully")
            }
    )
    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public Page<CardResponse> getCards(@RequestParam(defaultValue = "0", required = false) Integer offset,
                                       @RequestParam(defaultValue = "10", required = false) @Max(50) Integer limit){
        CardFilter cardFilter = new CardFilter(offset, limit);
        return cardService.getAllCards(cardFilter);
    }

    @Operation(
            summary = "Transfer cancel",
            description = "Cancellation of transfer",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transfer successfully cancel",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @DeleteMapping("transfer/cancel")
    @ResponseStatus(HttpStatus.OK)
    public void cancelTransfer(@RequestBody @Valid TransferResponse transferResponse){
        transferService.cancelTransfer(transferResponse);
    }
}