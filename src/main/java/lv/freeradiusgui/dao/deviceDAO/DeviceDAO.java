package lv.freeradiusgui.dao.deviceDAO;

import lv.freeradiusgui.domain.Device;

import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface DeviceDAO {

    boolean store(Device device);

    boolean storeAll(List<Device> deviceList);

    Device getById(Integer id);

    Device getByMac(String mac);

    List<Device> getAll();

    List<Device> getAllByCriteria(String fieldName, Object object);

    void delete(Device device);

    Long getCount();

}
