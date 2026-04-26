package lv.freeradiusgui.services;

import java.util.List;
import lv.freeradiusgui.domain.Account;

public interface AccountService {

    boolean store(Account account);

    Account getById(Integer id);

    Account getByLogin(String login);

    List<Account> getAll();

    boolean delete(Account account);

    Long getCount();

    Account prepareNewAccount();

    void fixRolesWithOutId(Account account);
}
