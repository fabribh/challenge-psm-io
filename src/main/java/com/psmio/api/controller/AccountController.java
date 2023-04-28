package com.psmio.api.controller;

import com.psmio.domain.model.Account;
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

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Create an account", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Account createAccount(@Validated @RequestBody Account account) {
        return accountService.addAccount(account);
    }

    @Operation(summary = "Get an account by id", method = "GET")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account Id not found"),
    })
    @GetMapping("/{accountId}")
    public Account getAccounts(@Validated @PathVariable Long accountId) {
        return accountService.getAccountsByIdOrElseThrow(accountId);
    }
}
