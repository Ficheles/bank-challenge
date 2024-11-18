package br.org.fideles.banking.mapper;

import org.springframework.stereotype.Component;
import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.model.Account;

@Component
public class AccountMapper {

    public Account fromEntity(AccountEntity accountEntity) {
        if (accountEntity == null) {
            return null;
        }

        return new Account(
                accountEntity.getId(),
                accountEntity.getAccountNumber(),
                accountEntity.getOwnerName(),
                accountEntity.getBalance()
        );

    }

    public AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(account.getId());
        accountEntity.setAccountNumber(account.getAccountNumber());
        accountEntity.setOwnerName(account.getOwnerName());
        accountEntity.setBalance(account.getBalance());

        return accountEntity;
    }
}
