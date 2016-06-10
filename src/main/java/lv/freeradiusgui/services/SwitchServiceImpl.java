package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.switchDAO.SwitchDAO;
import lv.freeradiusgui.domain.Switch;
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
    ClientsConfigFileService clientsConfigFileService;

    @Override
    public boolean store(Switch aSwitch) {
        return switchDAO.store(aSwitch);
    }

    @Override
    public boolean storeAll(List<Switch> switchList) {
        return switchDAO.storeAll(switchList);
    }


    @Override
    public Switch getById(Integer id) {
        return switchDAO.getById(id);
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
    public void reloadFromConfig() {
        List<String> list = clientsConfigFileService.readFile();
        List<Switch> listFromConfig = clientsConfigFileService.parseList(list);

        List<Switch> finalList = updateSwitchList(listFromConfig);

        switchDAO.storeAll(finalList);

    }

    private List<Switch> updateSwitchList(List<Switch> listFromConfig) {
        List<Switch> result = new ArrayList<>();
        List<Switch> listFromDB = switchDAO.getAll();

        for (Switch switchFromDB : listFromDB){
            Switch switchFromConfig = find(switchFromDB, listFromConfig);
            if (switchFromConfig != null ) {
                result.add(updateSwitch(switchFromDB, switchFromConfig));
                listFromConfig.remove(listFromConfig.indexOf(switchFromConfig)); //cant use remove because it is used for Hibernate
            }
        }

    }
}