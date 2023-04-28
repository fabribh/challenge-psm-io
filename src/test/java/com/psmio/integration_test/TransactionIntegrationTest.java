package com.psmio.integration_test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.psmio.api.model.TransactionDTO;
import com.psmio.domain.model.Account;
import com.psmio.domain.model.OperationType;
import com.psmio.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionIntegrationTest {
    public static final String URI = "/transactions";

    private Account account;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setupDataBase() {
        account = Account.builder().documentNumber("12345678900").build();
        accountRepository.save(account);
    }

    @Test
    void testCreateATransactionWithBuyTheCashTypeShouldSaveNegativeValue() throws Exception{
        var amount = BigDecimal.valueOf(5.55);
        var transaction = new TransactionDTO(account.getId(), OperationType.BUY_THE_CASH.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", is(transaction.accountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(transaction.operationTypeId())))
                .andExpect(jsonPath("$.amount").value(amount.negate()));
    }

    @Test
    void testCreateATransactionWithInstallmentPurchaseTypeShouldSaveNegativeValue() throws Exception{
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
    }

    @Test
    void testCreateATransactionWithWithdrawTypeShouldSaveNegativeValue() throws Exception{
        var amount = BigDecimal.valueOf(2.03);
        var transaction = new TransactionDTO(account.getId(), OperationType.WITHDRAW.getId(), amount);

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(transaction)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.account_id", is(transaction.accountId().intValue())))
                .andExpect(jsonPath("$.operation_type_id", is(transaction.operationTypeId())))
                .andExpect(jsonPath("$.amount").value(amount.negate()));
    }
    @Test
    void testCreateATransactionWithPaymentTypeShouldSavePositiveValue() throws Exception{
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
    }
}
