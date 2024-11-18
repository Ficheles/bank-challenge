package br.org.fideles.banking.service;

import br.org.fideles.banking.config.SecurityConfig;
import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.mapper.AccountMapper;
import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.model.User;
import br.org.fideles.banking.repository.AccountRepository;
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

    public Account findAccountById(Long accountId) {
        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(new AccountEntity());

        return accountMapper.toModel(accountEntity);
    }


    @Transactional
    public Account createAccount(Account account) {
        Long accountId = Optional.ofNullable(account.getId()).orElse(0L);
//        UserEntity = userMapper.to
        AccountEntity accountEntity = accountRepository.findById(accountId).orElse(new AccountEntity());

        accountEntity.setOwnerName(account.getOwnerName());
        accountEntity.setAccountNumber(account.getAccountNumber());

//        accountEntity.setBalance(Optional.ofNullable(account.getBalance()).orElse(BigDecimal.ZERO));

        accountRepository.save(accountEntity);

        return accountMapper.toModel(accountEntity);
    }

    @Transactional
    public AccountEntity credit(Long accountId, BigDecimal amount) {
//        AccountEntity account = accountRepository.findByIdWithLock(accountId)
//                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do crédito deve ser positivo.");
        }

        AccountEntity account = this.findById(accountId);
        account.setBalance(account.getBalance().add(amount));

        return accountRepository.save(account);
    }

    @Transactional
    public AccountEntity debit(Long accountId, BigDecimal amount) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do débito deve ser positivo.");
        }

        if (!hasEnoughFunds(account.getBalance(), amount)) {
            throw new RuntimeException("Insufficient funds in account: " + accountId);
        }

        account.setBalance(account.getBalance().subtract(amount));

        try {
            return accountRepository.save(account);  // O JPA vai usar a versão para controlar o bloqueio otimista
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("A conta foi modificada por outra transação. Tente novamente.", e);
        }
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

    private boolean hasEnoughFunds(BigDecimal balance, BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public List<Account> getAllAccounts() {
        Sort sort = Sort.by(Sort.Order.asc("ownerName"));  // Ordenação crescente pelo campo 'ownerName'

        List<AccountEntity> accountEntities = accountRepository.findAll(sort);

        return accountEntities.stream()
                .map(accountEntity -> new Account(accountEntity.getId(),
                        accountEntity.getAccountNumber(),
                        accountEntity.getOwnerName(),
                        accountEntity.getBalance()))
                .collect(Collectors.toList());
    }

    public Account updateAccount(Account account) {
        AccountEntity accountEntity = accountRepository.findById(account.getId()).orElse(new AccountEntity());

        accountEntity.setOwnerName(account.getOwnerName());
        accountEntity.setAccountNumber(account.getAccountNumber());

        accountRepository.save(accountEntity);

        return accountMapper.toModel(accountEntity);
    }
}