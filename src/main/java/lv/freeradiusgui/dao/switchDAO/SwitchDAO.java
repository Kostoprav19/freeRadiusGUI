package lv.freeradiusgui.dao.switchDAO;

import java.util.List;
import lv.freeradiusgui.domain.Switch;

public interface SwitchDAO {
    boolean store(Switch sSwitch);

    boolean storeAll(List<Switch> switchList);

    Switch getById(Integer id);

    Switch getByIp(String ip);

    List<Switch> getAll();

    List<Switch> getAllByCriteria(String fieldName, Object object);

    boolean delete(Switch aSwitch);

    Long getCount();
}
