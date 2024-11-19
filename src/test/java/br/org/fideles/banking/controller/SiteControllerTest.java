package br.org.fideles.banking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SiteControllerTest {

    @InjectMocks
    private SiteController siteController;

    @Mock
    private Model model;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(siteController).build();
    }

    @Test
    void testHomePage() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("pageTitle", "Página Inicial"))
                .andExpect(model().attribute("body", "pages/home"));
    }

    @Test
    void testAboutPage() throws Exception {
        mockMvc.perform(get("/sobre"))
                .andExpect(status().isOk())
                .andExpect(view().name("layout"))
                .andExpect(model().attribute("body", "pages/about"));
    }
}
