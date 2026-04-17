package lv.freeradiusgui.dao.deviceDAO;

import java.util.List;
import lv.freeradiusgui.domain.Device;

public interface DeviceDAO {
  boolean store(Device device);

  boolean storeAll(List<Device> deviceList);

  Device getById(Integer id);

  Device getByMac(String mac);

  List<Device> getAll();

  List<Device> getAllByCriteria(String fieldName, Object object);

  boolean delete(Device device);

  Long getCount();
}
