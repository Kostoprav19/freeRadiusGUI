package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.deviceDAO.DeviceDAO;
import lv.freeradiusgui.domain.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
@Service
public class DeviceServiceImpl implements DeviceService{

    @Autowired
    private DeviceDAO deviceDAO;

    @Override
    public boolean store(Device device) {
        return deviceDAO.store(device);
    }

    @Override
    public boolean storeAll(List<Device> deviceList) {
        return deviceDAO.storeAll(deviceList);
    }

    @Override
    public Device getById(Integer id) {
        return deviceDAO.getById(id);
    }

    @Override
    public Device getByMac(String mac) {
        return deviceDAO.getByMac(mac);
    }

    @Override
    public List<Device> getAll() {
        return deviceDAO.getAll();
    }

    @Override
    public List<Device> getAllByCriteria(String fieldName, Object object) {
        return deviceDAO.getAllByCriteria(fieldName, object);
    }

    @Override
    public void delete(Device device) {
        deviceDAO.delete(device);
    }

    @Override
    public Long getCount() {
        return deviceDAO.getCount();
    }

    @Override
    public Device prepareNewDevice() {
        Device device = new Device();
        device.setAccess(1); //Access-Accept
        device.setTimeOfRegistration(LocalDateTime.now());
        device.setSwitchPort(-1); //No information yet
        device.setPortSpeed(-1);
        return device;
    }

    @Override
    public boolean reloadFromConfig() {
        return false;
    }
}
