package lv.freeradiusgui.dao;

/**
 * Created by Dan on 23.04.2016.
 */
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

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

    public boolean store(Optional<T> entityOptional) {
        if (!entityOptional.isPresent()) {
            return false;
        }
        try {
            Session session = sessionFactory.getCurrentSession();
            session.saveOrUpdate(entityOptional.get());
            return true;
        } catch (Exception e) {
            System.out.println("Exception while execute AbstractGenericBaseDao.store()");
            System.out.println("------------------------------------------------------------------------");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------------------");
           // logger.error(e.getStackTrace());
            return false;
        }
    }

    public Optional<T> getById(String fieldName, Object object) {

        return getOneByCriteria(fieldName, object);
    }

    @SuppressWarnings("unchecked")
    protected Optional<T> getOneByCriteria(String fieldName, Object object) {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass);
        criteria.add(Restrictions.eq(fieldName, object));
        T obj = (T) criteria.uniqueResult();

        return Optional.ofNullable(obj);
    }

    protected List<T> getAllByCriteria(String fieldName, Object object) {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass);
        criteria.add(Restrictions.eq(fieldName, object));

        return criteria.list();
    }

    public List<T> getAll() {

        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(persistentClass);

        return criteria.list();
    }

    public void delete(Optional<T> entityOptional) {
        if (!entityOptional.isPresent()) {
            return;
        }
        Session session = sessionFactory.getCurrentSession();
        session.delete(entityOptional.get());
    }

    public int getCount(){
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(persistentClass);
        return criteria.list().size();
    }
}
