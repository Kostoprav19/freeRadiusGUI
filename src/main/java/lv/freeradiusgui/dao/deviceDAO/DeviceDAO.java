package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.dao.DBException;
import lv.freeradiusgui.domain.Device;

import java.util.List;
import java.util.Optional;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface DeviceDAO {

    boolean store(Optional<Device> deviceOptional);

    Optional<Device> getById(Long id);

    List<Device> getAll();

    List<Device> getAllByCriteria(String fieldName, Object object);

    void delete(Optional<Device> deviceOptional);

    Long getCount() throws DBException;

}
