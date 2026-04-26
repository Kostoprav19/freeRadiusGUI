package lv.freeradiusgui.services;

import java.util.ArrayList;
import java.util.List;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.repositories.SwitchRepository;
import lv.freeradiusgui.services.filesServices.ClientsConfFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwitchServiceImpl implements SwitchService {

    @Autowired private SwitchRepository switchRepository;

    @Autowired ClientsConfFileService clientsConfigFileService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(Switch aSwitch) {
        if (aSwitch == null) return false;
        try {
            switchRepository.save(aSwitch);
            return true;
        } catch (Exception e) {
            logger.error("Failed to store switch", e);
            return false;
        }
    }

    @Override
    public boolean storeAll(List<Switch> switchList) {
        if (switchList == null) return false;
        try {
            switchRepository.saveAll(switchList);
            logger.info("Successfully written switch records to database.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to write switch records to database", e);
            return false;
        }
    }

    @Override
    public Switch getById(Integer id) {
        if (id == null || id < 0) return null;
        return switchRepository.findById(id).orElse(null);
    }

    @Override
    public Switch getByIp(String ip) {
        if (ip == null || ip.isEmpty()) return null;
        return switchRepository.findByIp(ip);
    }

    @Override
    public Switch getByIp(String ip, List<Switch> list) {
        if (ip.isEmpty() || list.isEmpty()) return null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getIp().equals(ip)) return list.get(i);
        }
        return null;
    }

    @Override
    public List<Switch> getAll() {
        List<Switch> result = new ArrayList<>();
        switchRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public boolean delete(Switch aSwitch) {
        if (aSwitch == null) return false;
        try {
            switchRepository.delete(aSwitch);
            logger.info(
                    "Successfully deleted switch record from database. Switch id: "
                            + aSwitch.getId());
            return true;
        } catch (Exception e) {
            logger.error(
                    "Failed to delete switch records from database. Switch id: " + aSwitch.getId(),
                    e);
            return false;
        }
    }

    @Override
    public Long getCount() {
        return switchRepository.count();
    }

    @Override
    public Switch prepareNewSwitch() {
        return new Switch();
    }

    @Override
    public boolean reloadFromConfig() {
        List<Switch> listFromConfig = clientsConfigFileService.readListFromFile();
        if ((listFromConfig == null) || (listFromConfig.isEmpty())) return false;

        List<Switch> finalList = updateSwitchList(listFromConfig);
        if (!finalList.isEmpty()) storeAll(finalList);
        return true;
    }

    @Override
    public boolean writeToConfig() {
        List<Switch> listFromDB = getAll();
        if ((listFromDB == null) || (listFromDB.isEmpty())) return false;

        return clientsConfigFileService.saveListToFile(listFromDB);
    }

    private List<Switch> updateSwitchList(List<Switch> listFromConfig) {
        List<Switch> result = new ArrayList<>();

        for (Switch switchFromConfig : listFromConfig) {
            Switch switchFromDB = getByIp(switchFromConfig.getIp());
            if (switchFromDB != null) {
                switchFromDB.setName(switchFromConfig.getName());
                switchFromDB.setSecret(switchFromConfig.getSecret());
                result.add(switchFromDB);
            } else {
                result.add(switchFromConfig);
            }
        }
        return result;
    }
}
