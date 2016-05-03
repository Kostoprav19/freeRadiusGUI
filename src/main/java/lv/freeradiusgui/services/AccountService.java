package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Account;

import java.util.List;

public interface AccountService {

    boolean store(Account account);

    Account getById(Long id);

    Account getByLogin(String login);

    List<Account> getAll();

    List<Account> getAllByCriteria(String fieldName, Object object);

    void delete(Account account);

    Long getCount();

}


