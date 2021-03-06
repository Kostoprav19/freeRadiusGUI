package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Account;

import java.util.List;

public interface AccountService {

    boolean store(Account account);

    Account getById(Integer id);

    Account getByLogin(String login);

    List<Account> getAll();

    List<Account> getAllByCriteria(String fieldName, Object object);

    boolean delete(Account account);

    Long getCount();

    Account prepareNewAccount();

    void fixRolesWithOutId(Account account);

}


