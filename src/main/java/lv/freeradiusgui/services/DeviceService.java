package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Device;

import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
public interface DeviceService {

    boolean store(Device device);

    Device getById(Long id);

    List<Device> getAll();

    List<Device> getAllByCriteria(String fieldName, Object object);

    void delete(Device device);

    Long getCount();
}
