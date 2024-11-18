package br.org.fideles.banking.service;

import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrCreateUser_UserExists() {
        String username = "existingUser";
        String password = "password123";
        String role = "USER";

        UserEntity existingUser = new UserEntity();
        existingUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        UserEntity result = userService.getOrCreateUser(username, password, role);

        assertEquals(existingUser, result);
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testGetOrCreateUser_UserDoesNotExist() {
        String username = "newUser";
        String password = "password123";
        String role = "ADMIN";

        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword("encodedPassword");
        newUser.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(newUser);

        UserEntity result = userService.getOrCreateUser(username, password, role);

        assertEquals(newUser, result);
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(role, result.getRole());

        verify(userRepository, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }
}
