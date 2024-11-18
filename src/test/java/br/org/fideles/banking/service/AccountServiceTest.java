package br.org.fideles.banking.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.mapper.AccountMapper;
import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.repository.AccountRepository;
import br.org.fideles.banking.util.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private AccountEntity accountEntity;
    private String accountNumber1;
    private String accountNumber2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        accountNumber1 = "112233";
        accountNumber2 = "123231";
        accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setAccountNumber(accountNumber1);
        accountEntity.setOwnerName("Giovanna");
        accountEntity.setBalance(BigDecimal.valueOf(1000.00));
        accountEntity.setUser(new UserEntity(accountEntity.getOwnerName(),accountEntity.getOwnerName() + "123", Role.USER));
    }

    @Test
    public void testCreateAccount() {
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        Account account = new Account(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getOwnerName(), accountEntity.getBalance());
        when(accountMapper.toModel(any(AccountEntity.class))).thenReturn(account);

        Account result = accountService.createAccount(account, accountEntity.getUser());

        assertNotNull(result);
        assertEquals("112233", result.getAccountNumber());
        assertEquals("Giovanna", result.getOwnerName());
        assertEquals(BigDecimal.valueOf(1000.0), result.getBalance());

        verify(accountRepository, times(1)).save(any(AccountEntity.class));
        verify(accountMapper, times(1)).toModel(any(AccountEntity.class));
    }

    @Test
    public void testFindAccountById() {
        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(accountEntity));

        Account account = new Account(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getOwnerName(), accountEntity.getBalance());
        when(accountMapper.toModel(any(AccountEntity.class))).thenReturn(account);

        Account result = accountService.findAccountById(accountNumber1);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("112233", result.getAccountNumber());
        assertEquals("Giovanna", result.getOwnerName());
        assertEquals(BigDecimal.valueOf(1000.0), result.getBalance());

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber1);
        verify(accountMapper, times(1)).toModel(any(AccountEntity.class));
    }

    @Test
    public void testFindAccountByIdReturnNewAccountWhenNotFound() {
        when(accountRepository.findByAccountNumber(accountNumber2)).thenReturn(Optional.empty());

        Account result = accountService.findAccountById(accountNumber2);

        assertNotNull(result, "Expected a new Account object but got null.");
        assertEquals(0L, result.getId(), "ID should be 0 when account is not found.");
        assertNull(result.getAccountNumber(), "Account number should be null when account is not found.");
        assertNull(result.getOwnerName(), "Owner name should be null when account is not found.");
        assertEquals(BigDecimal.ZERO, result.getBalance(), "Balance should be zero for a new Account.");

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber2);
    }

    @Test
    public void testCreditAccount() {
        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(accountEntity));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        BigDecimal amount = BigDecimal.valueOf(500.00);
        AccountEntity updatedAccount = accountService.credit(accountNumber1, amount);

        assertNotNull(updatedAccount);
        assertEquals(BigDecimal.valueOf(1500.00), updatedAccount.getBalance());
    }

    @Test
    public void testDebitAccount() {
        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(accountEntity));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        BigDecimal amount = BigDecimal.valueOf(200.00);
        AccountEntity updatedAccount = accountService.debit(accountNumber1, amount);

        assertNotNull(updatedAccount);
        assertEquals(BigDecimal.valueOf(800.00), updatedAccount.getBalance());
    }

    @Test
    public void testCreditThrowsExceptionWhenAmountIsNegative() {
        BigDecimal amount = new BigDecimal("-10.00");

        assertThrows(IllegalArgumentException.class, () -> accountService.credit(accountNumber1, amount));
    }

    @Test
    public void testDebitAccountInsufficientFunds() {
        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(accountEntity));

        BigDecimal amount = BigDecimal.valueOf(2000.00);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.debit(accountNumber1, amount);
        });

        assertEquals("Insufficient funds in account: " + accountNumber1, exception.getMessage());
    }

    @Test
    public void testTransferSuccess() {
        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(accountEntity));

        AccountEntity toAccountEntity = new AccountEntity();
        toAccountEntity.setId(1L);
        toAccountEntity.setAccountNumber(accountNumber1);
        toAccountEntity.setOwnerName("Jane Doe");
        toAccountEntity.setBalance(BigDecimal.valueOf(500.00));

        when(accountRepository.findByAccountNumber(accountNumber2)).thenReturn(Optional.of(toAccountEntity));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        BigDecimal amount = BigDecimal.valueOf(200.00);
        accountService.transfer(accountNumber1, accountNumber2, amount);

        assertEquals(BigDecimal.valueOf(800.00), accountEntity.getBalance());
        assertEquals(BigDecimal.valueOf(700.00), toAccountEntity.getBalance());
    }

    @Test
    public void testTransferInsufficientFunds() {
        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(accountEntity));

        AccountEntity toAccountEntity = new AccountEntity();
        toAccountEntity.setId(2L);
        toAccountEntity.setAccountNumber("54321");
        toAccountEntity.setOwnerName("Jane Doe");
        toAccountEntity.setBalance(BigDecimal.valueOf(500.00));

        when(accountRepository.findByAccountNumber(accountNumber2)).thenReturn(Optional.of(toAccountEntity));

        BigDecimal amount = BigDecimal.valueOf(2000.00);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.transfer(accountNumber1, accountNumber2, amount);
        });

        assertEquals("Insufficient funds in account: " + accountNumber1, exception.getMessage());
    }

    @Test
    public void testDebitOptimisticLockingFailure() {
        BigDecimal amount = new BigDecimal("50.00");

        AccountEntity account = new AccountEntity();
        account.setId(1L);
        account.setBalance(new BigDecimal("100.00"));
        account.setVersion(1L);

        when(accountRepository.findByAccountNumber(accountNumber1)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenAnswer(invocation -> {
            AccountEntity savedAccount = invocation.getArgument(0);
            if (savedAccount.getVersion() != 1L) {
                throw new OptimisticLockingFailureException("Version conflict");
            }
            savedAccount.setVersion(savedAccount.getVersion() + 1);
            return savedAccount;
        });

        account.setVersion(2L);

        assertThrows(RuntimeException.class, () -> accountService.debit(accountNumber1, amount));
    }
}