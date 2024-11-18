package br.org.fideles.banking.service;

import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.mapper.UserMapper;
import br.org.fideles.banking.model.User;

import br.org.fideles.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
//    private  final UserMapper userMapper;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    @Transactional
    public UserEntity getOrCreateUser(String username, String password, String role) {

        return userRepository.findByUsername(username)
                .orElseGet(() -> {

                    UserEntity newUser = new UserEntity();
                    newUser.setUsername(username);
                    newUser.setPassword(passwordEncoder.encode(password));
                    newUser.setRole(role);

                    return userRepository.save(newUser);
                });

    }


//    public  boolean existsByUsername(String username) {
//        return userRepository.existsByUsername(username);
//    }

}
