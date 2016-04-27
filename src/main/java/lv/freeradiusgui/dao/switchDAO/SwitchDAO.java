package lv.freeradiusgui.dao.switchDAO;

import lv.freeradiusgui.dao.DBException;
import lv.freeradiusgui.domain.Switch;

import java.util.List;
import java.util.Optional;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface SwitchDAO {

    boolean store(Optional<Switch> switchOptional);

    Optional<Switch> getById(Long id);

    List<Switch> getAll();

    List<Switch> getAllByCriteria(String fieldName, Object object);

    void delete(Optional<Switch> switchOptional);

    Long getCount() throws DBException;

}
