package lv.freeradiusgui.dao.switchDAO;

import lv.freeradiusgui.dao.DBException;
import lv.freeradiusgui.domain.Switch;

import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface SwitchDAOInterface {

    void create(Switch aSwitch) throws DBException;

    Switch getById(Integer id) throws DBException;

    void delete(Integer id) throws DBException;

    void deleteAll() throws DBException;

    void update(Switch aSwitch) throws DBException;

    List<Switch> getAll(boolean sortOrder) throws DBException;

    int getCount() throws DBException;

}
