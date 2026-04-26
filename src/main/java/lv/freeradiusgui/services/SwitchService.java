package lv.freeradiusgui.services;

import java.util.List;
import lv.freeradiusgui.domain.Switch;

public interface SwitchService {
    boolean store(Switch sSwitch);

    boolean storeAll(List<Switch> switchList);

    Switch getById(Integer id);

    Switch getByIp(String ip);

    Switch getByIp(String ip, List<Switch> list);

    List<Switch> getAll();

    boolean delete(Switch aSwitch);

    Long getCount();

    Switch prepareNewSwitch();

    boolean reloadFromConfig();

    boolean writeToConfig();
}
