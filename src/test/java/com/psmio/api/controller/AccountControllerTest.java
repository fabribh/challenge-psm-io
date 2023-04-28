package com.psmio.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psmio.domain.exceptions.UserAccountNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    public static final String URI = "/accounts";
    public static final String PATH = "/{account_id}";

    @Autowired
    private WebApplicationContext web;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void testCreateAccount() throws Exception {
        var account = getAnAccountWithId(10L);

        when(accountService.addAccount(account))
                .thenReturn(account);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.document_number").value(account.getDocumentNumber()));
    }

    @Test
    void testGetAccountById() throws Exception {
        var accountId = 1L;
        var account = getAnAccountWithId(accountId);

        when(accountService.getAccountsByIdOrElseThrow(accountId))
                .thenReturn(account);

        mockMvc
                .perform(get(URI.concat(PATH), accountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId));
    }

    @Test
    void testFindAccountByIdWithNotExistAccountId() throws Exception {
        var accountId = 100L;
        doThrow(new UserAccountNotFoundException(accountId))
                .when(accountService).getAccountsByIdOrElseThrow(accountId);

        mockMvc
                .perform(get(URI.concat(PATH), accountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.status").value(is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.message")
                        .value(is("Account not found to an account_id: ".concat(String.valueOf(accountId)))));
    }
    private static Account getAnAccountWithId(Long accountId) {
        return Account.builder().id(accountId).documentNumber( "12345678900").build();
    }
}