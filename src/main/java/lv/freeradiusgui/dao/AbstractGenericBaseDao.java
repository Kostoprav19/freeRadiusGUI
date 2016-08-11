package lv.freeradiusgui.dao;

/**
 * Created by Dan on 23.04.2016.
 */

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional
public abstract class AbstractGenericBaseDao<T> {

    protected SessionFactory sessionFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public AbstractGenericBaseDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).
                getActualTypeArguments()[0];
    }

    public boolean store(T obj) {
        if (obj == null) {
            return false;
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.saveOrUpdate(obj);
            return true;
        } catch (Exception e) {
            logger.error("Exception while execute AbstractGenericBaseDao.store()");
            logger.error("STACK TRACE: ",e);
            return false;
        }
    }

    public boolean storeAll(List<T> list) {
        if (list == null) {
            return false;
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            for (T obj : list) {
                session.saveOrUpdate(obj);
            }
            return true;
        } catch (Exception e) {
            logger.error("Exception while execute AbstractGenericBaseDao.storeAll()");
            logger.error("STACK TRACE: ",e);
            return false;
        }
    }

    public T getById(String fieldName, Object object) {

        return getOneByCriteria(fieldName, object);
    }

    @SuppressWarnings("unchecked")
    protected T getOneByCriteria(String fieldName, Object object) {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass);
        criteria.add(Restrictions.eq(fieldName, object));

        List<T> list = criteria.list();
        T obj = list.isEmpty() ? null : (T) criteria.list().get(0);
        return obj;
    }

    protected T getLastByCriteria(String fieldName, Object object) {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass).addOrder(Order.desc("id"));
        criteria.add(Restrictions.eq(fieldName, object));

        List<T> list = criteria.list();
        T obj = list.isEmpty() ? null : (T) criteria.list().get(0);
        return obj;
    }

    public List<T> getAllByCriteria(String fieldName, Object object) {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass);
        criteria.add(Restrictions.eq(fieldName, object));
        //criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public List<T> getAll() {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass).addOrder(Order.asc("id"));
        //criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public void delete(T obj) {
        if (obj == null) {
            return;
        }
        Session session = sessionFactory.getCurrentSession();
        session.delete(obj);
    }

    public Long getCount(){
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass);
        //criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }
}
