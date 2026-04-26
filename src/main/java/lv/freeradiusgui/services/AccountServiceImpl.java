package lv.freeradiusgui.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.AccountRoleRef;
import lv.freeradiusgui.domain.Role;
import lv.freeradiusgui.repositories.AccountRepository;
import lv.freeradiusgui.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private AccountRepository accountRepository;

    @Autowired private RoleRepository roleRepository;

    @Autowired RoleService roleService;

    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public boolean store(Account account) {
        if (account == null) return false;
        fixPassword(account);
        // Form-bound roles arrive via Role(String) without an id; resolve ids
        // before rebuilding the persisted ref set, otherwise roleRefs end up
        // with roleId=null and the binding is silently dropped.
        fixRolesWithOutId(account);
        account.rebuildRoleRefs();
        try {
            accountRepository.save(account);
            return true;
        } catch (Exception e) {
            logger.error("Failed to store account", e);
            return false;
        }
    }

    @Override
    public Account getById(Integer id) {
        if (id == null || id < 0) return null;
        Account account = accountRepository.findById(id).orElse(null);
        hydrateRoles(account);
        return account;
    }

    @Override
    public Account getByLogin(String login) {
        if (login == null || login.isEmpty()) return null;
        Account account = accountRepository.findByLogin(login);
        hydrateRoles(account);
        return account;
    }

    @Override
    public List<Account> getAll() {
        List<Account> result = new ArrayList<>();
        accountRepository.findAll().forEach(result::add);
        hydrateRolesAll(result);
        return result;
    }

    @Override
    public boolean delete(Account account) {
        if (account == null) return false;
        try {
            accountRepository.delete(account);
            logger.info(
                    "Successfully deleted account record from database. Account id: "
                            + account.getId());
            return true;
        } catch (Exception e) {
            logger.error(
                    "Failed to delete account record from database. Account id: " + account.getId(),
                    e);
            return false;
        }
    }

    @Override
    public Long getCount() {
        return accountRepository.count();
    }

    @Override
    public Account prepareNewAccount() {
        Account account = new Account();
        account.setCreationDate(LocalDateTime.now());
        account.addRole(new Role(Role.ROLE_USER));
        account.setEnabled(true);
        return account;
    }

    @Override
    public void fixRolesWithOutId(Account account) {
        for (Role role : account.getRoles()) {
            if (role.getId() == null) {
                role.setId(roleService.getByName(role.getName()).getId());
            }
        }
    }

    private void fixPassword(Account account) {
        if ((account.getId() != null) && (account.getPassword().isEmpty())) {
            Account accountFromDB = accountRepository.findById(account.getId()).orElse(null);
            if (accountFromDB != null) account.setPassword(accountFromDB.getPassword());
        } else {
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
    }

    private void hydrateRoles(Account account) {
        if (account == null) return;
        Set<Integer> roleIds = new HashSet<>();
        for (AccountRoleRef ref : account.getRoleRefs()) {
            if (ref.getRoleId() != null) roleIds.add(ref.getRoleId());
        }
        if (roleIds.isEmpty()) {
            account.setRoles(new HashSet<>());
            return;
        }
        Set<Role> roles = new HashSet<>();
        roleRepository.findAllById(roleIds).forEach(roles::add);
        account.setRoles(roles);
    }

    private void hydrateRolesAll(List<Account> accounts) {
        if (accounts == null || accounts.isEmpty()) return;
        Set<Integer> allRoleIds = new HashSet<>();
        for (Account a : accounts) {
            for (AccountRoleRef ref : a.getRoleRefs()) {
                if (ref.getRoleId() != null) allRoleIds.add(ref.getRoleId());
            }
        }
        Map<Integer, Role> byId = new HashMap<>();
        if (!allRoleIds.isEmpty()) {
            roleRepository.findAllById(allRoleIds).forEach(r -> byId.put(r.getId(), r));
        }
        for (Account a : accounts) {
            Set<Role> roles = new HashSet<>();
            for (AccountRoleRef ref : a.getRoleRefs()) {
                Role r = byId.get(ref.getRoleId());
                if (r != null) roles.add(r);
            }
            a.setRoles(roles);
        }
    }
}
