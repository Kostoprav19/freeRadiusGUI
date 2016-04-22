package lv.freeradiusgui.database;

import lv.freeradiusgui.domain.Device;

import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface DeviceDAOInterface {

    void create(Device device) throws DBException;

    Device getById(Integer id) throws DBException;

    void delete(Integer id) throws DBException;

    void deleteAll() throws DBException;

    void update(Device device) throws DBException;

    List<Device> getAll(boolean sortOrder) throws DBException;

    int getCount() throws DBException;

}
