package lv.freeradiusgui.config;

import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.Role;
import lv.freeradiusgui.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("userDetailsService")
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    AccountService accountService;

    @Autowired
    public SecurityUserDetailsServiceImpl(AccountService accountService){

        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        Account account = accountService.getByLogin(login);

        if (account == null){
            throw new UsernameNotFoundException(login);
        }
        List<GrantedAuthority> authorities = buildUserAuthority(account.getRole());

        return buildUserForAuthentication(account, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<Role> roles) {

        Set<GrantedAuthority> auth = new HashSet<GrantedAuthority>();

        for (Role role : roles) {
            auth.add(new SimpleGrantedAuthority(role.getRole()));
        }

        return new ArrayList<GrantedAuthority>(auth);
    }

    private User buildUserForAuthentication(Account account, List<GrantedAuthority> auth) {

        return new User(
                account.getLogin(),
                account.getPassword(),
                account.isEnabled(),
                true, true, true,
                auth
        );
    }

}