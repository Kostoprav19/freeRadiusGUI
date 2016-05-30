package lv.freeradiusgui.dao.switchDAO;

import lv.freeradiusgui.dao.AbstractGenericBaseDao;
import lv.freeradiusgui.domain.Switch;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Dan on 23.04.2016.
 */

@Transactional
@Repository
public class SwitchDAOImpl extends AbstractGenericBaseDao<Switch> implements SwitchDAO {

    @Autowired
    public SwitchDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Switch getById(Integer id) {
        if (id == null || id < 0) {
            return null;
        }
        return getOneByCriteria("id", id);
    }

}
