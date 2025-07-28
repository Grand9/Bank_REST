package com.example.bankcards.controller;

import com.example.bankcards.dto.filter.CardFilter;
import com.example.bankcards.dto.request.CardTypeRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.card.CardService;
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
@RequestMapping("v1/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {
    private final CardService cardService;

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
    @PostMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse createCard(@PathVariable("ownerId") Long ownerId,
                                   @RequestBody @Valid CardTypeRequest cardTypeRequest) {
        log.info("Creating new card");
        return cardService.create(ownerId, cardTypeRequest);
    }

    @Operation(
            summary = "Set card status",
            description = "Change the status of a card",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card status successfully updated"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PostMapping("/{cardId}/status/update")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse changeCardStatus(@PathVariable("cardId")Long cardId,
                                         @RequestBody CardStatus cardStatus) {
        log.info("Changing status of a card");
        return cardService.changeStatus(cardId, cardStatus);
    }

    @Operation(
            summary = "Deleted card by ID",
            description = "Deleted cart by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable("cardId") Long cardId) {
        log.info("Deleting card by ID");
        cardService.deleteCard(cardId);
    }
    @Operation(
            summary = "Get card by ID",
            description = "Retrieves details of a card by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card found",
                            content = @Content(schema = @Schema(implementation = CardResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @GetMapping("/get/card/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse getCard(@PathVariable("cardId")Long cardId) {
        log.info("Retrieving card by ID");
        return cardService.findCardById(cardId);
    }

    @Operation(
            summary = "Blocked card",
            description = "Blocked card by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card blocked"),
                    @ApiResponse(responseCode = "404", description = "Card not found")
            }
    )
    @PostMapping("/block/{cardId}")
    @ResponseStatus(HttpStatus.OK)
    public CardResponse blockCard(@PathVariable("cardId")Long cardId){
        log.info("Blocking card by ID");
        return cardService.blockCard(cardId);
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
                    @ApiResponse(responseCode = "200", description = "Filtered card list returned successfully"),
                    @ApiResponse(responseCode = "404", description = "Filtered card list not found")
            }
    )
    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public Page<CardResponse> getAllCards(@RequestParam(defaultValue = "0", required = false)Integer offset,
                                          @RequestParam(defaultValue = "10", required = false) @Max(100) Integer limit) {
        log.info("Retrieving all cards");
        CardFilter  cardFilter = new CardFilter(offset, limit);
        return cardService.getAllCards(cardFilter);
    }
}