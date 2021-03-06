package lv.freeradiusgui.dao.accountDAO;

import lv.freeradiusgui.domain.Account;

import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface AccountDAO {

    boolean store(Account account);

    Account getById(Integer id);

    Account getByLogin(String login);

    List<Account> getAll();

    List<Account> getAllByCriteria(String fieldName, Object object);

    boolean delete(Account account);

    Long getCount();

}
