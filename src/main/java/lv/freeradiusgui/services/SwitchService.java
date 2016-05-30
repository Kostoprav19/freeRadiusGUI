package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Switch;

import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
public interface SwitchService {

    boolean store(Switch sSwitch);

    Switch getById(Integer id);

    List<Switch> getAll();

    List<Switch> getAllByCriteria(String fieldName, Object object);

    void delete(Switch aSwitch);

    Long getCount();

    Switch prepareNewSwitch();
}
