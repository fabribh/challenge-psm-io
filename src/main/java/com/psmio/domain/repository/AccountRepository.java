package com.psmio.domain.repository;

import com.psmio.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByDocumentNumber(String documentNumber);
}
