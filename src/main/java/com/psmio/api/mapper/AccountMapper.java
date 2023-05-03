package com.psmio.api.mapper;

import com.psmio.api.model.AccountDTO;
import com.psmio.domain.model.Account;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AccountMapper implements Function<AccountDTO, Account> {
    @Override
    public Account apply(AccountDTO accountDTO) {

        return Account.builder()
                .documentNumber(accountDTO.documentNumber())
                .availableCreditLimit(accountDTO.availableCreditLimit())
                .build();
    }
}
