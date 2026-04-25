package lv.freeradiusgui.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired private ApplicationEventPublisher applicationEventPublisher;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationEventPublisher(
                        new DefaultAuthenticationEventPublisher(applicationEventPublisher))
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Notes for future readers:
        //  - Role names are passed without the ROLE_ prefix because
        //    .hasRole(...) / .hasAnyRole(...) prepend it internally. The DB
        //    stores roles as ROLE_ADMIN / ROLE_USER
        //    (databaseCreationScript.sql); passing "ROLE_ADMIN" here would
        //    produce ROLE_ROLE_ADMIN checks and deny every request to the
        //    matched URL (fail-closed, not bypass).
        //  - /login* is explicitly permitAll. Spring Security 5 auto-permitted
        //    .formLogin().loginPage(...) targets; Spring Security 6 dropped
        //    that auto-permit, so without this rule the /login page itself
        //    requires authentication and the redirect chain loops infinitely.
        http.authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/login")
                                        .permitAll()
                                        .requestMatchers("/admin/**")
                                        .hasRole("ADMIN")
                                        .requestMatchers("/account/**")
                                        .hasRole("ADMIN")
                                        .requestMatchers("/server/**")
                                        .hasAnyRole("USER", "ADMIN")
                                        .requestMatchers("/switch/**")
                                        .hasAnyRole("USER", "ADMIN")
                                        .requestMatchers("/device/**")
                                        .hasAnyRole("USER", "ADMIN")
                                        .requestMatchers("/logs/**")
                                        .hasAnyRole("USER", "ADMIN")
                                        .anyRequest()
                                        .authenticated())
                .formLogin(
                        form ->
                                form.loginPage("/login")
                                        .failureUrl("/login?error")
                                        .loginProcessingUrl("/j_spring_security_check")
                                        .defaultSuccessUrl("/logs")
                                        .usernameParameter("j_username")
                                        .passwordParameter("j_password"))
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
