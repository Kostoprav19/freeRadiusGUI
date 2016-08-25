package lv.freeradiusgui.dao.logDAO;

import lv.freeradiusgui.dao.AbstractGenericBaseDao;
import lv.freeradiusgui.domain.Log;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Dan on 23.04.2016.
 */

@Transactional
@Repository
public class LogDAOImpl extends AbstractGenericBaseDao<Log> implements LogDAO {

    @Autowired
    public LogDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Log getById(Integer id) {
        if (id == null || id < 0) {
            return null;
        }
        return getOneByCriteria("id", id);
    }

    @Override
    public Log getLastByMac(String mac) {
        if (mac == null || mac.isEmpty()) {
            return null;
        }
        return getLastByCriteria("mac", mac);
    }

    @Override
    public Log getLast() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Log.class).addOrder(Order.desc("timeOfRegistration"));

        List<Log> list = criteria.list();
        Log obj = list.isEmpty() ? null : (Log) criteria.list().get(0);
        return obj;
    }

    @Override
    public List<Log> getByDate(LocalDateTime sDate, LocalDateTime eDate) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Log.class).addOrder(Order.asc("id"));
        criteria.add(Restrictions.ge("tor", sDate));
        criteria.add(Restrictions.lt("tor", eDate));
        //criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

}
