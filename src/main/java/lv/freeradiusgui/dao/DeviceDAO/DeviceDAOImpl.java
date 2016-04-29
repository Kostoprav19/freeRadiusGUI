package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.dao.AbstractGenericBaseDao;
import lv.freeradiusgui.domain.Device;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Dan on 23.04.2016.
 */

@Transactional
@Repository
public class DeviceDAOImpl extends AbstractGenericBaseDao<Device> implements DeviceDAO {

    @Autowired
    public DeviceDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Device getById(Long id) {
        if (id == null || id < 0) {
            return null;
        }
        return getOneByCriteria("id", id);
    }

}
