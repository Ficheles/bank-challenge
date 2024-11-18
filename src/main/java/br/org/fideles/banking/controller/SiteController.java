package br.org.fideles.banking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class SiteController {

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("pageTitle", "Página Inicial");
        model.addAttribute("body", "pages/home");

        return "layout";
    }

    @GetMapping("/sobre")
    public String aboutPage(Model model) {
        model.addAttribute("body", "pages/about");

        return "layout";
    }
}