package br.org.fideles.banking.config;

import br.org.fideles.banking.entity.CustomUserDetails;
import br.org.fideles.banking.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        logger.info("A operação foi realizada com sucesso!");

        http
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .antMatchers("/", "/login", "/sobre").permitAll()
                .antMatchers("/account", "/account/create", "/account/**/edit").hasRole("ADMIN")
                .antMatchers("/account/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .successHandler(customAuthenticationSuccessHandler())
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();

        return http.build();
    }

//    @Bean
//    protected void configure(HttpSecurity http) throws Exception {
//
//        System.out.println("Uadlfdas fkladjfl kasjd lkjasd");
//        logger.info("A operação foi realizada com sucesso!");
//        logger.error("Erro encontrado!");
//        logger.debug("Informações detalhadas para depuração.");
//
//        http
//                .authorizeRequests()
//                .antMatchers("/account/create").hasRole("ADMIN")  // Permite acesso à criação de conta apenas para ADMIN
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .defaultSuccessUrl("/account/create", true)
//                .permitAll();
//
//    }
//
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // Configuração de autenticação em memória para teste
//        auth.inMemoryAuthentication()
//                .withUser("admin").password("{noop}admin123").roles("ADMIN");
//    }

    private AuthenticationSuccessHandler customAuthenticationSuccessHandler() {



        return (request, response, authentication) -> {

            Object principal = authentication.getPrincipal();
            Long accountId = 0l;

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;
                accountId = userDetails.getAccountId();
            }

            String role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("");

            System.out.println("Role: " + role);
            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/account");
            } else if (role.equals("ROLE_USER")) {
                response.sendRedirect("/account/"+accountId+"/view");
            } else {
                response.sendRedirect("/");
            }
        };
    }
}





