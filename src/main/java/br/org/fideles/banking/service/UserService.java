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


//    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
//        this.userMapper = userMapper;
    }

    @Transactional
    public UserEntity createUser(String username, String password, String role) {
        UserEntity user = new UserEntity();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userRepository.save(user);
        System.out.println("Usuário " + username + " criado com sucesso.");

         return user;
    }


    public  boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
