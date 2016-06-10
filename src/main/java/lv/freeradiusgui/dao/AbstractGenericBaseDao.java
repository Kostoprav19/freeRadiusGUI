package lv.freeradiusgui.dao;

/**
 * Created by Dan on 23.04.2016.
 */
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

@Transactional
public abstract class AbstractGenericBaseDao<T> {

    protected SessionFactory sessionFactory;
    //private Logger logger = LogManager.getLogger(AbstractGenericBaseDao.class);
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
            System.out.println("Exception while execute AbstractGenericBaseDao.store()");
            System.out.println("------------------------------------------------------------------------");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------------------");
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
            System.out.println("Exception while execute AbstractGenericBaseDao.storeAll()");
            System.out.println("------------------------------------------------------------------------");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------------------");
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
        T obj = (T) criteria.uniqueResult();

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
