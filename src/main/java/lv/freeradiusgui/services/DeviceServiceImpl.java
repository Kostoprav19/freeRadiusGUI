package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.deviceDAO.DeviceDAO;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
@Service
public class DeviceServiceImpl implements DeviceService{

    @Autowired
    private DeviceDAO deviceDAO;

    @Autowired
    private UsersFileService usersFileService;

    @Autowired
    private LogService logService;

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
        List<Device> list = deviceDAO.getAll();
        list = updateStatistics(list);
        deviceDAO.storeAll(list);
        return list;
    }

    private List<Device> updateStatistics(List<Device> list) {
        List<Log> logList = logService.getAll();
        for (Device device : list){
            Log log = logService.getLastByMac(device.getMac(), logList);
            if (log != null) {
                device.setSwitch(log.getSwitch());
                device.setSwitchPort(log.getSwitchPort());
                device.setPortSpeed(log.getPortSpeed());
                device.setDuplex(log.getDuplex());
                device.setLastSeen(log.getTimeOfRegistration());
            }
        }
        return list;
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
    public Device prepareNewDevice(String mac) {
        Device device = new Device();
        if (mac != null) device.setMac(mac);
        device.setAccess(1); //Access-Accept
        device.setTimeOfRegistration(LocalDateTime.now());
        device.setSwitchPort(-1); //No information yet
        device.setPortSpeed(-1); //No information yet
        device.setDuplex(-1); //No information yet
        return device;
    }

    @Override
    public boolean reloadFromConfig() {
        List<Device> listFromFile = usersFileService.readFile();
        if (listFromFile == null) return false;

        List<Device> finalList = updateDeviceList(listFromFile);

        deviceDAO.storeAll(finalList);
        return true;
    }

    private List<Device> updateDeviceList(List<Device> listFromFile) {
        List<Device> result = new ArrayList<>();

        for (Device deviceFromConfig : listFromFile){
            Device switchFromDB = getByMac(deviceFromConfig.getMac());
            if (switchFromDB != null ) {
                switchFromDB.setName(deviceFromConfig.getName());
                switchFromDB.setAccess(deviceFromConfig.getAccess());
                switchFromDB.setType(deviceFromConfig.getType());
                if (deviceFromConfig.getTimeOfRegistration() == null) deviceFromConfig.setTimeOfRegistration(LocalDateTime.now());
                switchFromDB.setTimeOfRegistration(deviceFromConfig.getTimeOfRegistration());
                result.add(switchFromDB);
            } else {
                deviceFromConfig.setTimeOfRegistration(LocalDateTime.now());
                result.add(deviceFromConfig);
            }
        }
        return result;
    }
}
