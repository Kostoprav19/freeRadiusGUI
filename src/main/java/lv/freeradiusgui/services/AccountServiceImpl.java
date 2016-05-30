package lv.freeradiusgui.services;


import lv.freeradiusgui.dao.accountDAO.AccountDAO;
import lv.freeradiusgui.domain.Account;
import lv.freeradiusgui.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean store(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountDAO.store(account);
    }

    @Override
    public Account getById(Integer id) {
        return accountDAO.getById(id);
    }

    @Override
    public Account getByLogin(String login) {
        return accountDAO.getByLogin(login);
    }

    @Override
    public List<Account> getAll() {
        return accountDAO.getAll();
    }

    @Override
    public List<Account> getAllByCriteria(String fieldName, Object object) {
        return accountDAO.getAllByCriteria(fieldName, object);
    }

    @Override
    public void delete(Account account) {
        accountDAO.delete(account);
    }

    @Override
    public Long getCount() {
        return accountDAO.getCount();
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
        for (Role role: account.getRoles()){
            if (role.getId() == null) {
                role.setId(roleService.getByName(role.getName()).getId());
            }
        }
    }
}
