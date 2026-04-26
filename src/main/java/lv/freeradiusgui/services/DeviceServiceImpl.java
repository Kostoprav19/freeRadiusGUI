package lv.freeradiusgui.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.repositories.DeviceRepository;
import lv.freeradiusgui.repositories.SwitchRepository;
import lv.freeradiusgui.services.filesServices.UsersFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private DeviceRepository deviceRepository;

    @Autowired private SwitchRepository switchRepository;

    @Autowired private UsersFileService usersFileService;

    @Autowired private LogService logService;

    @Override
    public boolean store(Device device) {
        if (device == null) return false;
        try {
            deviceRepository.save(device);
            return true;
        } catch (Exception e) {
            logger.error("Failed to store device", e);
            return false;
        }
    }

    @Override
    public boolean storeAll(List<Device> deviceList) {
        if (deviceList == null) return false;
        try {
            deviceRepository.saveAll(deviceList);
            logger.info("Successfully written device records to database.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to write device records to database", e);
            return false;
        }
    }

    @Override
    public Device getById(Integer id) {
        if (id == null || id < 0) return null;
        Device device = deviceRepository.findById(id).orElse(null);
        hydrateSwitch(device);
        return device;
    }

    @Override
    public Device getByMac(String mac) {
        if (mac == null || mac.isEmpty()) return null;
        Device device = deviceRepository.findByMac(mac);
        hydrateSwitch(device);
        return device;
    }

    @Override
    public Device getByMac(String mac, List<Device> list) {
        if (mac.isEmpty() || list.isEmpty()) return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMac().equals(mac)) return list.get(i);
        }
        return null;
    }

    @Override
    public List<Device> getAll() {
        List<Device> list = new ArrayList<>();
        deviceRepository.findAll().forEach(list::add);
        hydrateSwitches(list);
        return list;
    }

    private List<Device> updateDeviceListStatistics(List<Device> deviceList, List<Log> logList) {
        for (Device device : deviceList) {
            Log log = logService.getLastByMac(device.getMac(), logList);
            if (log != null) {
                device.setSwitch(log.getSwitch());
                device.setSwitchPort(log.getSwitchPort());
                device.setPortSpeed(log.getPortSpeed());
                device.setDuplex(log.getDuplex());
                device.setLastSeen(log.getTimeOfRegistration());
            }
        }
        return deviceList;
    }

    @Override
    public boolean delete(Device device) {
        if (device == null) return false;
        try {
            deviceRepository.delete(device);
            logger.info(
                    "Successfully deleted device record from database. Switch id: "
                            + device.getId());
            return true;
        } catch (Exception e) {
            logger.error(
                    "Failed to delete device records from database. Switch id: " + device.getId(),
                    e);
            return false;
        }
    }

    @Override
    public Long getCount() {
        return deviceRepository.count();
    }

    @Override
    public Device prepareNewDevice(String mac) {
        Device device = new Device();
        if (mac != null) device.setMac(mac);
        device.setAccess(Device.ACCESS_ACCEPT);
        device.setTimeOfRegistration(LocalDateTime.now());
        device.setSwitchPort(-1);
        device.setPortSpeed(-1);
        device.setDuplex(-1);
        return device;
    }

    @Override
    public boolean reloadFromConfig() {
        List<Device> listFromFile = usersFileService.readListFromFile();
        if (listFromFile == null) return false;

        listFromFile = mergeWithDbRecords(listFromFile);

        List<Log> logList = logService.getToday();
        listFromFile = updateDeviceListStatistics(listFromFile, logList);
        deviceRepository.saveAll(listFromFile);
        return true;
    }

    @Override
    public boolean writeToConfig() {
        List<Device> listFromDB = getAll();
        if ((listFromDB == null) || (listFromDB.isEmpty())) return false;

        return usersFileService.saveListToFile(listFromDB);
    }

    @Override
    public void updateStatistics() {
        List<Device> deviceList = getAll();
        List<Log> logList = logService.getToday();
        deviceList = updateDeviceListStatistics(deviceList, logList);
        deviceRepository.saveAll(deviceList);
    }

    private List<Device> mergeWithDbRecords(List<Device> listFromFile) {
        List<Device> result = new ArrayList<>();
        List<Device> listFromDB = getAll();

        for (Device deviceFromFile : listFromFile) {
            Device deviceFromDB = getByMac(deviceFromFile.getMac(), listFromDB);
            if (deviceFromDB != null) {
                deviceFromDB.setName(deviceFromFile.getName());
                deviceFromDB.setAccess(deviceFromFile.getAccess());
                deviceFromDB.setType(deviceFromFile.getType());
                if (deviceFromFile.getTimeOfRegistration() == null)
                    deviceFromFile.setTimeOfRegistration(LocalDateTime.now());
                deviceFromDB.setTimeOfRegistration(deviceFromFile.getTimeOfRegistration());
                result.add(deviceFromDB);
            } else {
                deviceFromFile.setTimeOfRegistration(LocalDateTime.now());
                result.add(deviceFromFile);
            }
        }
        return result;
    }

    private void hydrateSwitch(Device device) {
        if (device == null || device.getSwitchId() == null) return;
        switchRepository.findById(device.getSwitchId()).ifPresent(device::setSwitch);
    }

    private void hydrateSwitches(List<Device> devices) {
        if (devices == null || devices.isEmpty()) return;
        Set<Integer> ids = new HashSet<>();
        for (Device d : devices) {
            if (d.getSwitchId() != null) ids.add(d.getSwitchId());
        }
        if (ids.isEmpty()) return;
        Map<Integer, Switch> byId = new HashMap<>();
        switchRepository.findAllById(ids).forEach(s -> byId.put(s.getId(), s));
        for (Device d : devices) {
            Integer sid = d.getSwitchId();
            if (sid != null) {
                Switch s = byId.get(sid);
                if (s != null) d.setSwitch(s);
            }
        }
    }
}
