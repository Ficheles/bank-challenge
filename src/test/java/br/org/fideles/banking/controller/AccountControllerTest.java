package br.org.fideles.banking.controller;

import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.service.AccountService;
import br.org.fideles.banking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void testShowAccountList() throws Exception {
        List<Account> mockAccounts = Arrays.asList(
                new Account(1L, "123456", "Joãozinho", BigDecimal.valueOf(1000)),
                new Account(2L, "654321", "Zequinha", BigDecimal.valueOf(2000))
        );

        when(accountService.getAllAccounts()).thenReturn(mockAccounts);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().attribute("body", "account/list"));

        verify(accountService, times(1)).getAllAccounts();
    }

    @Test
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/account/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("body", "account/create"));
    }

    @Test
    void testCreateAccount() throws Exception {
        mockMvc.perform(post("/account/create")
                        .param("ownerName", "Joãozinho")
                        .param("accountNumber", "123456")
                        .param("balance", "1000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account"));

        verify(accountService, times(1)).createAccount(any(Account.class), any());
    }

    @Test
    void testEditAccountForm() throws Exception {
        Account mockAccount = new Account(1L, "123456", "Joãozinho", BigDecimal.valueOf(1000));

        when(accountService.findAccountById("1")).thenReturn(mockAccount);

        mockMvc.perform(get("/account/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("body", "account/edit"));

        verify(accountService, times(1)).findAccountById("1");
    }

    @Test
    void testViewAccountForm() throws Exception {
        Account mockAccount = new Account(1L, "123456", "Joãozinho", BigDecimal.valueOf(1000));

        when(accountService.findAccountById("1")).thenReturn(mockAccount);

        mockMvc.perform(get("/account/1/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("body", "account/view"));

        verify(accountService, times(1)).findAccountById("1");
    }
}
