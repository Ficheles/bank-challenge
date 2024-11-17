package br.org.fideles.banking.controller;

import br.org.fideles.banking.service.AccountService;
import br.org.fideles.banking.model.Account;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @GetMapping("/create")
    public String showCreateForm(Long accountId) {
        accountService.findAccountById(accountId);

        return "account/create";
    }

    @PostMapping("/create")
    public String createAccount(@ModelAttribute Account account) {
        accountService.createAccount(account.getOwnerName(), account.getAccountNumber());
        return "redirect:/account";
    }

    @PostMapping("/{id}/credit")
    public String credit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        accountService.credit(id, amount);
        return "redirect:/account/" + id;
    }

    @PostMapping("/{id}/debit")
    public String debit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        accountService.debit(id, amount);
        return "redirect:/account/" + id;
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {

        accountService.transfer(fromAccountId, toAccountId, amount);
        return "redirect:/account";
    }

}