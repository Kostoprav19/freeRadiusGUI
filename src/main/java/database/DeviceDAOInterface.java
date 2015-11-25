package database;

import domain.Device;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface DeviceDAOInterface {

    void create(Device device) throws DBException;

    Device getById(Long id) throws DBException;

    void delete(Long id) throws DBException;

    void deleteAll() throws DBException;

    void update(Device device) throws DBException;

    List<Device> getAll(boolean sortOrder) throws DBException;

    int getCount() throws DBException;

}
