package lv.freeradiusgui.services;


import lv.freeradiusgui.dao.accountDAO.AccountDAO;
import lv.freeradiusgui.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDAO accountDAO;

    @Override
    public boolean store(Account account) {
        return accountDAO.store(account);
    }

    @Override
    public Account getById(Long id) {
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
}
