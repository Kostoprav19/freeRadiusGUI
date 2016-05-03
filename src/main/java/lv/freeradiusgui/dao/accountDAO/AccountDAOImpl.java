package lv.freeradiusgui.dao.accountDAO;

import lv.freeradiusgui.dao.AbstractGenericBaseDao;
import lv.freeradiusgui.domain.Account;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Dan on 23.04.2016.
 */

@Transactional
@Repository
public class AccountDAOImpl extends AbstractGenericBaseDao<Account> implements AccountDAO {

    @Autowired
    public AccountDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Account getById(Long id) {
        if (id == null || id < 0) {
            return null;
        }
        return getOneByCriteria("id", id);
    }

    @Override
    public Account getByLogin(String login) {
        if (login == null || login.isEmpty()) {
            return null;
        }
        return getOneByCriteria("login", login);
    }

}
