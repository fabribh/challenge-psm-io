package com.psmio.api.controller;

import com.psmio.api.mapper.AccountMapper;
import com.psmio.api.mapper.AccountModelMapper;
import com.psmio.api.model.AccountDTO;
import com.psmio.domain.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Account")
public class AccountController {

    private final AccountService accountService;

    private final AccountMapper mapper;

    private final AccountModelMapper modelMapper;

    public AccountController(AccountService accountService, AccountMapper mapper, AccountModelMapper modelMapper) {
        this.accountService = accountService;
        this.mapper = mapper;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Create an account", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created"),
            @ApiResponse(responseCode = "400", description = "Available credit limit must be greater than zero"),
            @ApiResponse(responseCode = "400", description = "Document number must not be blank"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDTO createAccount(@Validated @RequestBody AccountDTO accountDTO) {
        var account = mapper.apply(accountDTO);
        var accountCreated = modelMapper.apply(accountService.addAccount(account));
        return accountCreated;
    }

    @Operation(summary = "Get an account by id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account Id not found"),
    })
    @GetMapping("/{accountId}")
    public AccountDTO getAccounts(@Validated @PathVariable Long accountId) {
        var accountFound = modelMapper.apply(accountService.getAccountsByIdOrElseThrow(accountId));
        return accountFound;
    }
}
