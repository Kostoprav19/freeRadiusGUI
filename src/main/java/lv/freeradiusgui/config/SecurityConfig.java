package lv.freeradiusgui.config;

/**
 * Created by Dan on 01.05.2016.
 */
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {

        auth
                .authenticationEventPublisher(new DefaultAuthenticationEventPublisher(applicationEventPublisher))
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                //.and().authenticationProvider(authenticationProvider)
                ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/account/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/server/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/switch/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/device/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/logs/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
                .and()
                .formLogin().loginPage("/login").failureUrl("/login?error")
                .loginProcessingUrl("/j_spring_security_check")
                .defaultSuccessUrl("/logs")
                .usernameParameter("j_username")
                .passwordParameter("j_password")
                .and()
                .logout().logoutSuccessUrl("/login?logout")
                .and()
                .csrf();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder;
    }
}