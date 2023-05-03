package com.psmio.integration_test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.psmio.api.model.TransactionDTO;
import com.psmio.domain.model.Account;
import com.psmio.domain.model.OperationType;
import com.psmio.domain.repository.AccountRepository;
import com.psmio.domain.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionIntegrationTest {
    public static final String URI = "/transactions";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void testCreateATransactionWithBuyTheCashTypeShouldSaveNegativeValueAlsoUpdateLimitAccount() throws Exception{
        var account = createAccount("74185296322", new BigDecimal("500"));
        var amount = new BigDecimal("500");
        var transaction = new TransactionDTO(account.getId(), OperationType.BUY_THE_CASH.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", is(transaction.accountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(transaction.operationTypeId())))
                .andExpect(jsonPath("$.amount").value(amount.negate()));

        var accountUpdated = getAccountById(account.getId());
        assertEquals(accountUpdated.getAvailableCreditLimit().doubleValue(), BigDecimal.ZERO.doubleValue());
    }

    @Test
    void testCreateATransactionWithInstallmentPurchaseTypeShouldSaveNegativeValueAlsoUpdateLimitAccount() throws Exception{
        var account = createAccount("95175365482", new BigDecimal("1100"));
        var amount = BigDecimal.valueOf(1080.56);
        var transaction = new TransactionDTO(account.getId(), OperationType.INSTALLMENT_PURCHASE.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", is(transaction.accountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(transaction.operationTypeId())))
                .andExpect(jsonPath("$.amount").value(amount.negate()));

        var accountUpdated = getAccountById(account.getId());
        assertEquals(accountUpdated.getAvailableCreditLimit(), new BigDecimal("19.44"));
    }

    @Test
    void testCreateATransactionWithWithdrawTypeShouldSaveNegativeValueAlsoUpdateLimitAccount() throws Exception{
        var account = createAccount("48627593155", new BigDecimal("3"));
        var amount = BigDecimal.valueOf(2.99);
        var transaction = new TransactionDTO(account.getId(), OperationType.WITHDRAW.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", is(transaction.accountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(transaction.operationTypeId())))
                .andExpect(jsonPath("$.amount").value(amount.negate()));

        var accountUpdated = getAccountById(account.getId());
        assertEquals(accountUpdated.getAvailableCreditLimit(), new BigDecimal("0.01"));
    }

    @Test
    void testCreateATransactionWithPaymentTypeShouldSavePositiveValueAlsoUpdateLimitAccount() throws Exception{
        var account = createAccount("73916341577", new BigDecimal("40"));
        var amount = BigDecimal.valueOf(25.55);
        var transaction = new TransactionDTO(account.getId(), OperationType.PAYMENT.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", is(transaction.accountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(transaction.operationTypeId())))
                .andExpect(jsonPath("$.amount").value(amount));

        var accountUpdated = getAccountById(account.getId());
        assertEquals(accountUpdated.getAvailableCreditLimit(), new BigDecimal("65.55"));
    }
    @Test
    void testCreateATransactionWithAmountValueZeroShouldThrowAnException() throws Exception{
        var account = createAccount("44556688211", new BigDecimal("500"));
        var amount = BigDecimal.ZERO;
        var transaction = new TransactionDTO(account.getId(), OperationType.PAYMENT.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.fields[0].message")
                        .value(is(TransactionService.MUST_BE_GREATER_THAN_0.concat(amount.toString()))));
    }

    @Test
    void testAmountValueGreaterThanTheLimitAndOperationBuyTheCashShouldThrowAnException() throws Exception{
        var account = createAccount("02705144550", new BigDecimal("5000"));
        var amount = new BigDecimal("5001");
        var transaction = new TransactionDTO(account.getId(), OperationType.BUY_THE_CASH.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.message")
                        .value(is(TransactionService.LIMIT_IS_NOT_ENOUGH.concat(amount.toString()))));
    }

    @Test
    void testAmountValueGreaterThanTheLimitAndOperationInstallmentPurchaseShouldThrowAnException() throws Exception{
        var account = createAccount("6632844550", new BigDecimal("5000"));
        var amount = new BigDecimal("5000.01");
        var transaction = new TransactionDTO(account.getId(), OperationType.INSTALLMENT_PURCHASE.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.message")
                        .value(is(TransactionService.LIMIT_IS_NOT_ENOUGH.concat(amount.toString()))));
    }

    @Test
    void testAmountValueGreaterThanTheLimitAndOperationWithdrawShouldThrowAnException() throws Exception{
        var account = createAccount("11223344550", new BigDecimal("5000"));
        var amount = new BigDecimal("5000.001");
        var transaction = new TransactionDTO(account.getId(), OperationType.WITHDRAW.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.name())))
                .andExpect(jsonPath("$.message")
                        .value(is(TransactionService.LIMIT_IS_NOT_ENOUGH.concat(amount.toString()))));
    }

    private Account createAccount(String documentNumber, BigDecimal limit) {
        var account = Account.builder()
                .documentNumber(documentNumber)
                .availableCreditLimit(new BigDecimal(String.valueOf(limit)))
                .build();
        return accountRepository.save(account);
    }

    private Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId).get();
    }
}
