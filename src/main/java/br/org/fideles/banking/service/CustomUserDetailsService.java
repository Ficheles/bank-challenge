package br.org.fideles.banking.service;

import br.org.fideles.banking.entity.AccountEntity;
import br.org.fideles.banking.entity.CustomUserDetails;
import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.repository.AccountRepository;
import br.org.fideles.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        AccountEntity account = accountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada para o usuário"));

        return new CustomUserDetails(
                user.getId(),
                account.getAccountNumber(),
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}