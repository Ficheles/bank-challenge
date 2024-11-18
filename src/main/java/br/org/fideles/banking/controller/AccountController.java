package br.org.fideles.banking.controller;

import br.org.fideles.banking.entity.UserEntity;
import br.org.fideles.banking.service.AccountService;
import br.org.fideles.banking.model.Account;
import br.org.fideles.banking.service.UserService;
import br.org.fideles.banking.util.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    public AccountController(
            AccountService accountService,
            UserService userService
            ) {
        this.accountService = accountService;
        this.userService = userService;
    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("")
    public String showAccountList(Model model) {
        List<Account> accounts = accountService.getAllAccounts();

        model.addAttribute("accounts", accounts);
        model.addAttribute("body", "account/list");

        return "layout";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Account account = new Account();

        model.addAttribute("account", account);
        model.addAttribute("body", "account/create");

        return "layout";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createAccount(Account account) {
        UserEntity user = userService.getOrCreateUser(account.getOwnerName(), account.getOwnerName(), Role.USER);
        accountService.createAccount(account, user);

        return "redirect:/account";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{accountId}/edit")
    public String editAccountForm(@PathVariable String accountId, Model model) {
        Account account = accountService.findAccountById(accountId);

        model.addAttribute("account", account);
        model.addAttribute("body", "account/edit");

        return "layout";
    }


    @GetMapping("/{accountId}/view")
    public String viewAccountForm(@PathVariable String accountId, Model model) {
        Account account = accountService.findAccountById(accountId);

        model.addAttribute("account", account);
        model.addAttribute("body", "account/view");

        return "layout";
    }


    @GetMapping("/{accountId}/credit")
    public String creditForm(@PathVariable Long accountId, Model model) {
        model.addAttribute("accountId", accountId);
        model.addAttribute("body", "account/credit");

        return "layout";
    }

    @PostMapping("/credit")
    public String credit( @RequestParam String accountId, @RequestParam BigDecimal amount) {
        accountService.credit(accountId, amount);

        return "redirect:/account/" + accountId +"/view";
    }

    @GetMapping("/{accountId}/debit")
    public String debitForm(@PathVariable Long accountId, Model model) {
        model.addAttribute("accountId", accountId);
        model.addAttribute("body", "account/debit");

        return "layout";
    }

    @PostMapping("/debit")
    public String debit(@RequestParam String accountId, @RequestParam BigDecimal amount) {
        accountService.debit(accountId, amount);

        return "redirect:/account/" + accountId +"/view";
    }

    @GetMapping("/{fromAccountId}/transfer")
    public String transfer(@PathVariable String fromAccountId, Model model) {
        model.addAttribute("fromAccountId", fromAccountId);
        model.addAttribute("body", "account/transfer");

        return "layout";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String fromAccountId,
            @RequestParam String toAccountId,
            @RequestParam BigDecimal amount) {

        accountService.transfer(fromAccountId, toAccountId, amount);
        return "redirect:/account/" + fromAccountId +"/view";
    }

}