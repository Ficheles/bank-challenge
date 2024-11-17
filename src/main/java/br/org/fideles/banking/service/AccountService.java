package br.org.fideles.banking.service;

import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.mapper.AccountMapper;
import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.repository.AccountRepository;
//import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service

public class AccountService {
//    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Account findAccountById(Long accountId) {
        AccountEntity accountEntity =  accountRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("Account not found: " + accountId)
        );

        return new Account(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getOwnerName(), accountEntity.getBalance());
//        return accountMapper.fromEntity(accountEntity);
    }

    private AccountEntity findAccountEntityById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("Account not found: " + accountId)
        );
    }

    @Transactional
    public Account createAccount(String ownerName, String accountNumber) {

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setOwnerName(ownerName);
        accountEntity.setAccountNumber(accountNumber);
        accountEntity.setBalance(BigDecimal.ZERO);

        accountRepository.save(accountEntity);
        return new Account(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getOwnerName(), accountEntity.getBalance());

//        return accountMapper.fromEntity(accountEntity);
    }

    @Transactional
    public AccountEntity credit(Long accountId, BigDecimal amount) {
//        AccountEntity account = accountRepository.findByIdWithLock(accountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do crédito deve ser positivo.");
        }

        AccountEntity account = this.findAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amount));

        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity debit(Long accountId, BigDecimal amount) {
//         Não utiliza o version;
//        AccountEntity account = accountRepository.findByIdWithLock(accountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do débito deve ser positivo.");
        }

        AccountEntity account = this.findAccountEntityById(accountId);

        if (!hasEnoughFunds(account.getBalance(), amount)) {
            throw new RuntimeException("Insufficient funds in account: " + accountId);
        }

        account.setBalance(account.getBalance().subtract(amount));

         accountRepository.save(account);

        return account;
    }


    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {

//        AccountEntity fromAccount = this.findAccountEntityById(fromAccountId);
//        AccountEntity toAccount = this.findAccountEntityById(toAccountId);
//
//
//        if (!hasEnoughFunds(fromAccount.getBalance(), amount)) {
//            throw new RuntimeException("Insufficient funds in account: " + fromAccountId);
//        }
//
//        // Deduct amount from source account
//        debit(fromAccount, amount);
//        // Add amount to destination account
//        credit(toAccount, amount);



        // Deduct amount from source account
        this.debit(fromAccountId, amount);
        // Add amount to destination account
        this.credit(toAccountId, amount);

//        accountRepository.save(fromAccount);
//        accountRepository.save(toAccount);
    }


    private  boolean hasEnoughFunds(BigDecimal balance, BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

}