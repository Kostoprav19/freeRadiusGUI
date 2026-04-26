package lv.freeradiusgui.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lv.freeradiusgui.config.WebMVCConfig;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.AccountRoleRef;
import lv.freeradiusgui.domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = WebMVCConfig.class)
@Transactional
@Rollback
public class AccountRepositoryTest {

    @Autowired private AccountRepository accountRepository;

    @Autowired private RoleRepository roleRepository;

    /**
     * T3: AccountRoleRef must persist into the legacy {@code accounts_roles} table, not the
     * snake-case default {@code account_role_ref}. Failure here is a {@code
     * BadSqlGrammarException}.
     */
    @Test
    public void roleRefRoundTripUsesAccountsRolesTable() {
        Role admin = roleRepository.findByName(Role.ROLE_ADMIN);
        assertNotNull(admin, "seed role ROLE_ADMIN missing");

        Account account = new Account();
        account.setLogin("test-roleref-" + System.nanoTime());
        account.setPassword("ignored");
        account.setEnabled(true);
        account.setCreationDate(LocalDateTime.now());
        Set<Role> roles = new HashSet<>();
        roles.add(admin);
        account.setRoles(roles);

        Account saved = accountRepository.save(account);
        assertNotNull(saved.getId());

        Account reloaded = accountRepository.findById(saved.getId()).orElse(null);
        assertNotNull(reloaded);
        assertEquals(1, reloaded.getRoleRefs().size());
        AccountRoleRef ref = reloaded.getRoleRefs().iterator().next();
        assertEquals(admin.getId(), ref.getRoleId());
    }
}
