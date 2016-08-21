package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.switchDAO.SwitchDAO;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.services.filesServices.ClientsConfFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
@Service
public class SwitchServiceImpl implements SwitchService{

    @Autowired
    private SwitchDAO switchDAO;

    @Autowired
    ClientsConfFileService clientsConfigFileService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(Switch aSwitch) {
        return switchDAO.store(aSwitch);
    }

    @Override
    public boolean storeAll(List<Switch> switchList) {
        boolean result = switchDAO.storeAll(switchList);
        if (result) {
            logger.info("Successfully written switch records to database.");
        } else {
            logger.error("Failed to write switch records to database");
        }
        return result;
    }

    @Override
    public Switch getById(Integer id) {
        return switchDAO.getById(id);
    }

    @Override
    public Switch getByIp(String ip) {
        return switchDAO.getByIp(ip);
    }

    @Override
    public List<Switch> getAll() {
        return switchDAO.getAll();
    }

    @Override
    public List<Switch> getAllByCriteria(String fieldName, Object object) {
        return switchDAO.getAllByCriteria(fieldName, object);
    }

    @Override
    public void delete(Switch aSwitch) {
        switchDAO.delete(aSwitch);
    }

    @Override
    public Long getCount() {
        return switchDAO.getCount();
    }

    @Override
    public Switch prepareNewSwitch() {
        Switch aSwitch = new Switch();
        return aSwitch;
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
        List<Switch> listFromDB = switchDAO.getAll();
        if ((listFromDB == null) || (listFromDB.isEmpty())) return false;

        return clientsConfigFileService.saveListToFile(listFromDB);
    }

    private List<Switch> updateSwitchList(List<Switch> listFromConfig) {
        List<Switch> result = new ArrayList<>();

        for (Switch switchFromConfig : listFromConfig){
            Switch switchFromDB = getByIp(switchFromConfig.getIp());
            if (switchFromDB != null ) {
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