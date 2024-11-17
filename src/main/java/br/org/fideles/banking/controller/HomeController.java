package br.org.fideles.banking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Rota para a página inicial
    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("message", "Bem-vindo ao sistema bancário!");

        return "home";  // Nome do arquivo Thymeleaf (home.html)
    }
}