package br.org.fideles.banking.service;

import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.mapper.AccountMapper;
import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.repository.AccountRepository;
import br.org.fideles.banking.exception.InsufficientFundsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;


    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    public AccountService(AccountRepository accountRepository , AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }


    private AccountEntity findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("Account not found: " + accountId)
        );
    }

    private AccountEntity findAccountEntityById(String accountId) {
        return accountRepository.findByAccountNumber(accountId).orElseThrow(
                () -> new RuntimeException("Account not found: " + accountId)
        );
    }

    public Account findAccountById(String accountId) {
        try {
            return accountMapper.toModel( this.findAccountEntityById(accountId));
        }catch (Exception e) {
            return new Account();
        }
    }


    @Transactional
    public Account createAccount(Account account, UserEntity user) {
        Long accountId = Optional.ofNullable(account.getId()).orElse(0L);
        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(new AccountEntity(user));

        accountEntity.setOwnerName(account.getOwnerName());
        accountEntity.setAccountNumber(account.getAccountNumber());

        accountEntity.setBalance(Optional.ofNullable(account.getBalance()).orElse(BigDecimal.ZERO));

        accountRepository.save(accountEntity);

        return accountMapper.toModel(accountEntity);
    }

    @Transactional
    public AccountEntity credit(String accountId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do crédito deve ser positivo.");
        }

        AccountEntity account = this.findAccountEntityById(accountId);
        account.setBalance(account.getBalance().add(amount));

        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity debit(String accountId, BigDecimal amount) {
        AccountEntity account = accountRepository.findByAccountNumber(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do débito deve ser positivo.");
        }

        if (!hasEnoughFunds(account.getBalance(), amount)) {
            throw new InsufficientFundsException("Insufficient funds in account: " + accountId);
        }

        account.setBalance(account.getBalance().subtract(amount));

        try {
            return accountRepository.save(account);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("A conta foi modificada por outra transação. Tente novamente.", e);
        }
    }


    @Transactional
    public void transfer(String fromAccountId, String toAccountId, BigDecimal amount) {
        this.debit(fromAccountId, amount);
        this.credit(toAccountId, amount);
    }

    private boolean hasEnoughFunds(BigDecimal balance, BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public List<Account> getAllAccounts() {
        Sort sort = Sort.by(Sort.Order.asc("ownerName")); 

        List<AccountEntity> accountEntities = accountRepository.findAll(sort);

        return accountEntities.stream()
                .map(accountEntity -> new Account(accountEntity.getId(),
                        accountEntity.getAccountNumber(),
                        accountEntity.getOwnerName(),
                        accountEntity.getBalance()))
                .collect(Collectors.toList());
    }
}