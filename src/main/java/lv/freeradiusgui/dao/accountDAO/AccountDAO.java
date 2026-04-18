package lv.freeradiusgui.dao.accountDAO;

import java.util.List;
import lv.freeradiusgui.domain.Account;

public interface AccountDAO {
    boolean store(Account account);

    Account getById(Integer id);

    Account getByLogin(String login);

    List<Account> getAll();

    List<Account> getAllByCriteria(String fieldName, Object object);

    boolean delete(Account account);

    Long getCount();
}
