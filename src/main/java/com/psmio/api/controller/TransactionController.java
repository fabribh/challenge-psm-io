package com.psmio.api.controller;

import com.psmio.api.mapper.TransactionMapper;
import com.psmio.api.mapper.TransactionModelMapper;
import com.psmio.api.model.TransactionDTO;
import com.psmio.domain.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/transactions", produces = {"application/json"})
@Tag(name = "Transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionModelMapper modelMapper;
    private final TransactionMapper mapper;

    public TransactionController(TransactionService transactionService, TransactionModelMapper modelMapper, TransactionMapper mapper) {
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
        this.mapper = mapper;
    }

    @Operation(summary = "Create a transaction to an account", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction created successfully",
            content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Operation Type invalid or Account Id not found"),
            @ApiResponse(responseCode = "400", description = "Transaction Operation invalid"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDTO createTransaction(@Validated @RequestBody TransactionDTO transactionDTO) {

        var transaction = mapper.apply(transactionDTO);

        transaction = transactionService.createTransaction(transaction);
        transactionDTO = modelMapper.apply(transaction);
        return transactionDTO;
    }
}
