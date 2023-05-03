package com.psmio.api.mapper;

import com.psmio.api.model.AccountDTO;
import com.psmio.domain.model.Account;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountModelMapper implements Function<Account, AccountDTO> {
    @Override
    public AccountDTO apply(Account account) {
        return new AccountDTO(account.getDocumentNumber(),account.getAvailableCreditLimit());
    }
}
