package com.psmio.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psmio.api.mapper.TransactionMapper;
import com.psmio.api.mapper.TransactionModelMapper;
import com.psmio.api.model.TransactionDTO;
import com.psmio.domain.exceptions.OperationTypeNotFoundException;
import com.psmio.domain.exceptions.UserAccountNotFoundException;
import com.psmio.domain.model.Account;
import com.psmio.domain.model.OperationType;
import com.psmio.domain.model.Transaction;
import com.psmio.domain.service.TransactionService;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({TransactionController.class, TransactionMapper.class, TransactionModelMapper.class})
class TransactionControllerTest {

    public static final String URI = "/transactions";

    @Autowired
    private WebApplicationContext web;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private TransactionMapper modelMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void testCreateTransaction() throws Exception{
        var transactionDTO = new TransactionDTO(1L, 4, new BigDecimal("123.45"));
        var transaction = Transaction.builder()
                .transaction_id(10L)
                .account(Account.builder().id(transactionDTO.accountId()).build())
                .operationType(OperationType.getById(transactionDTO.operationTypeId()))
                .amount(transactionDTO.amount())
                .build();

        when(modelMapper.apply(transactionDTO)).thenReturn(transaction);
        when(transactionService.createTransaction(any(Transaction.class)))
                .thenReturn(transaction);

        mockMvc
                .perform(post(URI)
                        .content(mapper.writeValueAsString(transactionDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id").value(transactionDTO.accountId()))
                .andExpect(jsonPath("$.operation_type_id").value(transactionDTO.operationTypeId()))
                .andExpect(jsonPath("$.amount").value(transactionDTO.amount()));
    }

    @Test
    void testCreateTransactionWithNonexistentAccountId() throws Exception{
        var transactionDTO = new TransactionDTO(100L, 4, new BigDecimal("123.45"));
        var transaction = Transaction.builder()
                .account(Account.builder().id(transactionDTO.accountId()).build())
                .operationType(OperationType.getById(transactionDTO.operationTypeId()))
                .amount(transactionDTO.amount())
                .build();

        when(modelMapper.apply(transactionDTO)).thenReturn(transaction);
        doThrow(new UserAccountNotFoundException(transactionDTO.accountId()))
                .when(transactionService).createTransaction(any(Transaction.class));

        mockMvc
                .perform(post(URI)
                        .content(mapper.writeValueAsString(transactionDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.status").value(is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.message")
                        .value(is("Account not found to an account_id: "
                                .concat(String.valueOf(transactionDTO.accountId())))));
    }

    @Test
    void testCreateTransactionWithInvalidOperationTypeId() throws Exception{
        var transactionDTO = new TransactionDTO(1L, 9, new BigDecimal("123.45"));

        doThrow(new OperationTypeNotFoundException(transactionDTO.operationTypeId()))
                .when(modelMapper).apply(transactionDTO);

        mockMvc
                .perform(post(URI)
                        .content(mapper.writeValueAsString(transactionDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.status").value(is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.message")
                        .value(is("Operation Type not found for an operation_type_id: "
                                .concat(String.valueOf(transactionDTO.operationTypeId())))));
    }
}