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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        accountEntity = new AccountEntity();
        accountEntity.setId(1L);
        accountEntity.setAccountNumber("112233");
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
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountEntity));

        Account account = new Account(accountEntity.getId(), accountEntity.getAccountNumber(), accountEntity.getOwnerName(), accountEntity.getBalance());
        when(accountMapper.toModel(any(AccountEntity.class))).thenReturn(account);

        Account result = accountService.findAccountById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("112233", result.getAccountNumber());
        assertEquals("Giovanna", result.getOwnerName());
        assertEquals(BigDecimal.valueOf(1000.0), result.getBalance());

        verify(accountRepository, times(1)).findById(1L);
        verify(accountMapper, times(1)).toModel(any(AccountEntity.class));
    }

    @Test
    public void testFindAccountByIdReturnNewAccountWhenNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        when(accountMapper.toModel(any(AccountEntity.class))).thenReturn(new Account());

        Account result = accountService.findAccountById(1L);

        assertNotNull(result, "Expected a new Account object but got null.");
        assertEquals(0L, result.getId(), "ID should be 0 when account is not found.");
        assertNull(result.getAccountNumber(), "Account number should be null when account is not found.");
        assertNull(result.getOwnerName(), "Owner name should be null when account is not found.");
        assertEquals(BigDecimal.ZERO, result.getBalance(), "Balance should be zero for a new Account.");

        verify(accountRepository, times(1)).findById(1L);
        verify(accountMapper, times(1)).toModel(any(AccountEntity.class));
    }

    @Test
    public void testCreditAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountEntity));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        BigDecimal amount = BigDecimal.valueOf(500.00);
        AccountEntity updatedAccount = accountService.credit(1L, amount);

        assertNotNull(updatedAccount);
        assertEquals(BigDecimal.valueOf(1500.00), updatedAccount.getBalance());
    }

    @Test
    public void testDebitAccount() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountEntity));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        BigDecimal amount = BigDecimal.valueOf(200.00);
        AccountEntity updatedAccount = accountService.debit(1L, amount);

        assertNotNull(updatedAccount);
        assertEquals(BigDecimal.valueOf(800.00), updatedAccount.getBalance());
    }

    @Test
    public void testCreditThrowsExceptionWhenAmountIsNegative() {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("-10.00");

        assertThrows(IllegalArgumentException.class, () -> accountService.credit(accountId, amount));
    }

    @Test
    public void testDebitAccountInsufficientFunds() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountEntity));

        BigDecimal amount = BigDecimal.valueOf(2000.00);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.debit(1L, amount);
        });

        assertEquals("Insufficient funds in account: 1", exception.getMessage());
    }

    @Test
    public void testTransferSuccess() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountEntity));

        AccountEntity toAccountEntity = new AccountEntity();
        toAccountEntity.setId(2L);
        toAccountEntity.setAccountNumber("54321");
        toAccountEntity.setOwnerName("Jane Doe");
        toAccountEntity.setBalance(BigDecimal.valueOf(500.00));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccountEntity));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        BigDecimal amount = BigDecimal.valueOf(200.00);
        accountService.transfer(1L, 2L, amount);

        assertEquals(BigDecimal.valueOf(800.00), accountEntity.getBalance());
        assertEquals(BigDecimal.valueOf(700.00), toAccountEntity.getBalance());
    }

    @Test
    public void testTransferInsufficientFunds() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountEntity));

        AccountEntity toAccountEntity = new AccountEntity();
        toAccountEntity.setId(2L);
        toAccountEntity.setAccountNumber("54321");
        toAccountEntity.setOwnerName("Jane Doe");
        toAccountEntity.setBalance(BigDecimal.valueOf(500.00));

        when(accountRepository.findById(2L)).thenReturn(Optional.of(toAccountEntity));

        BigDecimal amount = BigDecimal.valueOf(2000.00);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.transfer(1L, 2L, amount);
        });

        assertEquals("Insufficient funds in account: 1", exception.getMessage());
    }

    @Test
    public void testDebitOptimisticLockingFailure() {

        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("50.00");

        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        account.setBalance(new BigDecimal("100.00"));
        account.setVersion(1L);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(AccountEntity.class))).thenAnswer(invocation -> {
            AccountEntity savedAccount = invocation.getArgument(0);
            if (savedAccount.getVersion() != 1L) {
                throw new OptimisticLockingFailureException("Version conflict");
            }
            savedAccount.setVersion(savedAccount.getVersion() + 1);
            return savedAccount;
        });

        account.setVersion(2L);

        assertThrows(RuntimeException.class, () -> accountService.debit(accountId, amount));
    }
}