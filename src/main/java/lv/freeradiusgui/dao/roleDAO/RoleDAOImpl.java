package lv.freeradiusgui.dao.roleDAO;

import lv.freeradiusgui.dao.AbstractGenericBaseDao;
import lv.freeradiusgui.domain.Role;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Dan on 26.05.2016.
 */

@Transactional
@Repository
public class RoleDAOImpl extends AbstractGenericBaseDao<Role> implements RoleDAO {
    @Autowired
    public RoleDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public Role getById(Integer id) {
        if (id == null || id < 0) {
            return null;
        }
        return getOneByCriteria("id", id);
    }

    @Override
    public Role getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return getOneByCriteria("name", name);
    }

}
