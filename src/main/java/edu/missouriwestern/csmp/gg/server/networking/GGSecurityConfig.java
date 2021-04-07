package edu.missouriwestern.csmp.gg.server.networking;

import edu.missouriwestern.csmp.gg.server.controllers.PlayerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class GGSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PlayerController playerController;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder)
                .withUser("user").password(passwordEncoder.encode("password")).roles("USER")
                .and()
                .withUser("admin").password(passwordEncoder.encode("password")).roles("USER", "ADMIN");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/index.html").permitAll()
                .antMatchers("/game/**").hasRole("USER")
                .antMatchers("/board/**").hasRole("USER")
                .antMatchers("/tile/**").hasRole("USER")
                .antMatchers("/entity/**").hasRole("USER")
                .antMatchers("/container/**").hasRole("USER")
                //.antMatchers("/index.html").hasAnyRole("ADMIN", "USER")
                .and().formLogin()
                .successHandler(playerController)
                .and().logout().logoutSuccessUrl("/index.html").permitAll()
                .and().csrf().disable();
    }
}
