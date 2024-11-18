package br.org.fideles.banking;

import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.mapper.AccountMapper;
import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.repository.AccountRepository;
import br.org.fideles.banking.repository.UserRepository;
import br.org.fideles.banking.service.AccountService;
import br.org.fideles.banking.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

//@SpringBootApplication
@SpringBootApplication(scanBasePackages = "br.org.fideles.banking")
@ComponentScan(basePackages = "br.org.fideles.banking")
@EntityScan(basePackages = "br.org.fideles.banking.entity")
@EnableJpaRepositories(basePackages = "br.org.fideles.banking.repository")
@EnableTransactionManagement
public class BankingApplication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

    @PostConstruct
    public void init() {
        createUserIfNotExists("admin", "admin123", Role.ADMIN);
        createUserIfNotExists("user", "user123", Role.USER);

//        createAccountIfNotExists("Joh Smith", "12345", new BigDecimal(50L));
//        createAccountIfNotExists("Fulano", "12345", new BigDecimal(0L));
//        createAccountIfNotExists("Beltrano", "12345", new BigDecimal(10L));
    }

    private void createUserIfNotExists(String username, String password, String role) {
        if (!userRepository.existsByUsername(username)) {
            UserEntity user = new UserEntity();

            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);

            userRepository.save(user);
            System.out.println("Usuário " + username + " criado com sucesso.");
        }
    }

//    private void createAccountIfNotExists(String accountNumber, String ownerName, BigDecimal balance) {
//        if (!accountRepository.existsByAccountNumber(accountNumber)) {
//            AccountEntity account = new AccountEntity();
//
//            account.setAccountNumber(accountNumber);
//            account.setOwnerName(ownerName);
//            account.setBalance(balance);
//
//            accountRepository.save(account);
//            System.out.println("Account " + ownerName + " criado com sucesso.");
//        }
//    }
//
//    private void test(long accountId ) {
//        AccountService service = new AccountService(accountRepository, new AccountMapper());
//
//        System.out.println("-----------------------------------------------------------------------------");
//        Account account = service.findAccountById(accountId);
//        System.out.println("Account: " + account);
//        System.out.println("-----------------------------------------------------------------------------");
//    }
}



