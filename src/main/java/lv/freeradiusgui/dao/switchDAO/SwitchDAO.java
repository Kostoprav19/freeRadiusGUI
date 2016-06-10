package lv.freeradiusgui.dao.switchDAO;

import lv.freeradiusgui.domain.Switch;
import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface SwitchDAO {

    boolean store(Switch sSwitch);

    boolean storeAll(List<Switch> switchList);

    Switch getById(Integer id);

    List<Switch> getAll();

    List<Switch> getAllByCriteria(String fieldName, Object object);

    void delete(Switch aSwitch);

    Long getCount();

}
