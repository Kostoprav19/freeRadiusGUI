package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.dao.AbstractGenericBaseDao;
import lv.freeradiusgui.domain.Device;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created by Dan on 23.04.2016.
 */
@Transactional
public class DeviceDAOImpl extends AbstractGenericBaseDao<Device> implements DeviceDAOInterface {

    @Autowired
    public DeviceDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Device> getById(Long id) {
        if (id == null || id < 0) {
            return Optional.empty();
        }
        return getOneByCriteria("id", id);
    }

}
