package br.org.fideles.banking;

import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.repository.UserRepository;
import br.org.fideles.banking.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(basePackages = "br.org.fideles.banking")
@EntityScan(basePackages = "br.org.fideles.banking.entity")
@EnableJpaRepositories(basePackages = "br.org.fideles.banking.repository")
@EnableTransactionManagement
public class BankingApplication {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

    @PostConstruct
    public void init() {
        createUserIfNotExists("admin", "admin123", Role.ADMIN);
        createUserIfNotExists("user", "user123", Role.USER);
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
}



