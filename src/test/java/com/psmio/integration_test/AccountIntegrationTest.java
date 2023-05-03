package com.psmio.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psmio.domain.model.Account;
import com.psmio.domain.repository.AccountRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountIntegrationTest {

    public static final String URI = "/accounts";
    public static final String PATH = "/{accountId}";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AccountRepository repository;

    @Test
    void testCreateAnAccount() throws Exception {
        var account = Account.builder()
                .documentNumber("98765432100")
                .availableCreditLimit(new BigDecimal("5000"))
                .build();

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.available_credit_limit").value(account.getAvailableCreditLimit().doubleValue()))
                .andExpect(jsonPath("$.document_number", is(account.getDocumentNumber())));

    }

    @Test
    void testFindAccountById() throws Exception {
        var accountCreated = createAccount("14523698700");
        var account = repository.findById(accountCreated.getId()).get();

        mockMvc
                .perform(get(URI.concat(PATH), accountCreated.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available_credit_limit").value(account.getAvailableCreditLimit().doubleValue()))
                .andExpect(jsonPath("$.document_number", is(account.getDocumentNumber())));
    }

    @Test
    void testFindAccountByIdAccountNotFoundException() throws Exception {
        mockMvc
                .perform(get(URI.concat(PATH), 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode", is(HttpStatus.NOT_FOUND.value())))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.name())))
                .andExpect(jsonPath("$.message", is("Account not found to an account_id: 100")));
    }

    private Account createAccount(String documentNumber) {
        var account = Account.builder()
                .documentNumber(documentNumber)
                .availableCreditLimit(new BigDecimal("5000"))
                .build();
        return repository.save(account);
    }
}
