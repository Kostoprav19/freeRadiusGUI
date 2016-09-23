package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.deviceDAO.DeviceDAO;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.services.filesServices.UsersFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
        boolean result =  deviceDAO.storeAll(deviceList);

        if (result) {
            logger.info("Successfully written device records to database.");
        } else {
            logger.error("Failed to write device records to database");
        }
        return result;
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
    public Device getByMac(String mac, List<Device> list) {
        if (mac.isEmpty() || list.isEmpty() ) return null;
        for (int i = 0; i < list.size(); i++){
            if (list.get(i).getMac().equals(mac)) return list.get(i);
        }
        return null;
    }

    @Override
    public List<Device> getAll() {
        List<Device> list = deviceDAO.getAll();
        return list;
    }

    private List<Device> updateDeviceListStatistics(List<Device> list) {
        List<Log> logList = logService.getToday();
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
    public boolean delete(Device device) {
        boolean result = deviceDAO.delete(device);
        if (result) {
            logger.info("Successfully deleted device record from database. Switch id: " + device.getId());
        } else {
            logger.error("Failed to delete device records from database. Switch id: " + device.getId());
        }
        return result;
    }

    @Override
    public Long getCount() {
        return deviceDAO.getCount();
    }

    @Override
    public Device prepareNewDevice(String mac) {
        Device device = new Device();
        if (mac != null) device.setMac(mac);
        device.setAccess(Device.ACCESS_ACCEPT);
        device.setTimeOfRegistration(LocalDateTime.now());
        device.setSwitchPort(-1); //No information yet
        device.setPortSpeed(-1); //No information yet
        device.setDuplex(-1); //No information yet
        return device;
    }

    @Override
    public boolean reloadFromConfig() {
        List<Device> listFromFile = usersFileService.readListFromFile();
        if (listFromFile == null) return false;

        listFromFile = mergeWithDbRecords(listFromFile);
        listFromFile = updateDeviceListStatistics(listFromFile);
        deviceDAO.storeAll(listFromFile);
        return true;
    }

    @Override
    public boolean writeToConfig() {
        List<Device> listFromDB = deviceDAO.getAll();
        if ((listFromDB == null) || (listFromDB.isEmpty())) return false;

        return usersFileService.saveListToFile(listFromDB);
    }

    @Override
    public void updateStatistics() {

    }

    private List<Device> mergeWithDbRecords(List<Device> listFromFile) {
        List<Device> result = new ArrayList<>();
        List<Device> listFromDB = getAll();

        for (Device deviceFromFile : listFromFile){
            Device deviceFromDB = getByMac(deviceFromFile.getMac(), listFromDB);
            if (deviceFromDB != null ) {
                deviceFromDB.setName(deviceFromFile.getName());
                deviceFromDB.setAccess(deviceFromFile.getAccess());
                deviceFromDB.setType(deviceFromFile.getType());
                if (deviceFromFile.getTimeOfRegistration() == null) deviceFromFile.setTimeOfRegistration(LocalDateTime.now());
                deviceFromDB.setTimeOfRegistration(deviceFromFile.getTimeOfRegistration());
                result.add(deviceFromDB);
            } else {
                deviceFromFile.setTimeOfRegistration(LocalDateTime.now());
                result.add(deviceFromFile);
            }
        }
        return result;
    }
}
