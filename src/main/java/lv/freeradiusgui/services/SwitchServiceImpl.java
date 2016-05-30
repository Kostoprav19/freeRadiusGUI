package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.switchDAO.SwitchDAO;
import lv.freeradiusgui.domain.Switch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
@Service
public class SwitchServiceImpl implements SwitchService{

    @Autowired
    private SwitchDAO switchDAO;

    @Override
    public boolean store(Switch aSwitch) {
        return switchDAO.store(aSwitch);
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
}