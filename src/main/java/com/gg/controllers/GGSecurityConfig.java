package com.gg.controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;



@EnableWebSecurity
public class GGSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 =
                User.withDefaultPasswordEncoder()
                        .username("user")
                        .password("password")
                        .roles("USER")
                        .build();
        UserDetails user2 =
                User.withDefaultPasswordEncoder()
                        .username("user2")
                        .password("password")
                        .roles("USER")
                        .build();
        UserDetails user3 =
                User.withDefaultPasswordEncoder()
                        .username("user3")
                        .password("password")
                        .roles("USER")
                        .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);

    }
}
