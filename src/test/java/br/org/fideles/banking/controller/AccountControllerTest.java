package br.org.fideles.banking.controller;

import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private Account account;

    @BeforeEach
    public void setUp() {
        // Setup do Account para os testes
        account = new Account(1L,"123456","John Doe", BigDecimal.ZERO  );
    }

    @Test
    @WithMockUser(username = "admin", password = "admin123", roles = {"ADMIN"})
    public void testShowCreateForm() throws Exception {

        Mockito.when(accountService.findAccountById(anyString())).thenReturn(account);


        mockMvc.perform(get("/account/create")
                        .param("accountId", "1"))
                .andExpect(status().isOk()) // Espera-se um status HTTP 200 OK
                .andExpect(view().name("account/create")) // A view esperada é "account/create"
                .andExpect(model().attributeExists("account")) // Verifica se o atributo "account" está no modelo
                .andExpect(model().attribute("account", account)); // Verifica se o valor de "account" no modelo é o esperado

        verify(accountService).findAccountById(anyString());
    }
//
//    @Test
//    public void testShowCreateForm() throws Exception {
//
//        Mockito.when(accountService.findAccountById(anyLong())).thenReturn(account);
//
//        mockMvc.perform(get("/account/create").param("accountId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("account/create"));
//
//        verify(accountService).findAccountById(anyLong());
//    }

//    @Test
//    public void testCreateAccount() throws Exception {
//        // Simular a criação da conta
//        Mockito.doNothing().when(accountService).createAccount(any(), any());
//
//        mockMvc.perform(post("/account/create")
//                        .param("ownerName", "John Doe")
//                        .param("accountNumber", "123456"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/account"));
//
//        // Verificar se o método do service foi chamado corretamente
//        verify(accountService).createAccount("John Doe", "123456");
//    }
//
//    @Test
//    public void testCredit() throws Exception {
//        // Simular o crédito
//        Mockito.doNothing().when(accountService).credit(anyLong(), any(BigDecimal.class));
//
//        mockMvc.perform(post("/account/1/credit")
//                        .param("amount", "100.00"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/account/1"));
//
//        // Verificar se o método do service foi chamado corretamente
//        verify(accountService).credit(1L, new BigDecimal("100.00"));
//    }
//
//    @Test
//    public void testDebit() throws Exception {
//        // Simular o débito
//        Mockito.doNothing().when(accountService).debit(anyLong(), any(BigDecimal.class));
//
//        mockMvc.perform(post("/account/1/debit")
//                        .param("amount", "50.00"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/account/1"));
//
//        // Verificar se o método do service foi chamado corretamente
//        verify(accountService).debit(1L, new BigDecimal("50.00"));
//    }
//
//    @Test
//    public void testTransfer() throws Exception {
//        // Simular a transferência
//        Mockito.doNothing().when(accountService).transfer(anyLong(), anyLong(), any(BigDecimal.class));
//
//        mockMvc.perform(post("/account/transfer")
//                        .param("fromAccountId", "1")
//                        .param("toAccountId", "2")
//                        .param("amount", "200.00"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/account"));
//
//        // Verificar se o método do service foi chamado corretamente
//        verify(accountService).transfer(1L, 2L, new BigDecimal("200.00"));
//    }
}
