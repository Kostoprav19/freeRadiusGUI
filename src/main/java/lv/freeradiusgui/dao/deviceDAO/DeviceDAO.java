package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.dao.DBException;
import lv.freeradiusgui.domain.Device;

import java.util.Optional;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface DeviceDAO {

    boolean store(Optional<Device> deviceOptional);

    Optional<Device> getById(Long id);

    void delete(Optional<Device> deviceOptional);

    Long getCount() throws DBException;

}
