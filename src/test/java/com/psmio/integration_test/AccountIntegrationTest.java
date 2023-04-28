package com.psmio.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psmio.domain.model.Account;
import com.psmio.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
    @BeforeEach
    void setupDataBase() {
        var account = Account.builder().documentNumber("12345678900").build();
        repository.save(account);
    }

    @Test
    void testCreateAnAccount() throws Exception {
        var account = Account.builder().documentNumber("98765432100").build();

        mockMvc
                .perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.document_number", is(account.getDocumentNumber())));

    }

    @Test
    void testFindAccountById() throws Exception {
        var userId = 1L;
        var account = repository.findById(userId).get();

        mockMvc
                .perform(get(URI.concat(PATH), 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId().intValue())))
                .andExpect(jsonPath("$.document_number", is(account.getDocumentNumber())));
    }

    @Test
    void testFindAccountByIdAccountNotFoundException() throws Exception {
        mockMvc
                .perform(get(URI.concat(PATH), 100L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.httpStatus", is("NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("Account not found to an account_id: 100")));
    }
}
