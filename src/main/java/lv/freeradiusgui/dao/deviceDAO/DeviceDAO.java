package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.domain.Device;
import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface DeviceDAO {

    boolean store(Device device);

    Device getById(Integer id);

    List<Device> getAll();

    List<Device> getAllByCriteria(String fieldName, Object object);

    void delete(Device device);

    Long getCount();

}
