package database.jdbc.sqlite;

import database.DBException;
import database.DeviceDAOInterface;
import database.SwitchDAOInterface;
import domain.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */
@Component
public class DeviceDAOImpl extends DAOImpl implements DeviceDAOInterface {

    public static final String DB_NAME = "devices";
    public static final String DB_FIELDS = "id = ?, mac = ?, name = ?, descr = ?, switch_id = ?, port = ?, speed = ?, tor = ?, lastseen = ?, access = ?";

    @Autowired
    private SwitchDAOInterface switchDAO;

    public void create(Device device) throws DBException {
        if (device == null) {
            return;
        }

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO " + DB_NAME + " VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            prepareStatement(device, preparedStatement);

            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()){
                device.setId(rs.getInt(1));
            }
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOImpl.create()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public Device getById(Integer id) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM " + DB_NAME + " WHERE LogID = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Device device = null;
            if (resultSet.next()) {
                device = setLogEntry(resultSet);
            }
            return device;
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOImpl.getById()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void delete(Integer id) throws DBException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + DB_NAME + " WHERE LogID = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOImpl.delete()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public void deleteAll() throws DBException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + DB_NAME);
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOImpl.deleteAll()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void update(Device device) throws DBException {
        if (device == null) {
            return;
        }
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE " + DB_NAME + " SET " + DB_FIELDS + " WHERE LogID = ?");
            prepareStatement(device, preparedStatement);
            preparedStatement.setInt(6, device.getId());

            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOImpl.update()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<Device> getAll(boolean sortOrder) throws DBException {
        List<Device> list = new ArrayList<Device>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + DB_NAME);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Device device = setLogEntry(resultSet);
                list.add(device);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list DeviceDAOImpl.getAll()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    private List<Device> filter(String criteria) throws DBException {
        List<Device> list = new ArrayList<Device>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + DB_NAME + " WHERE " + criteria);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Device device = setLogEntry(resultSet);
                list.add(device);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list LogEntrysDAOImpl.filter()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    private List<Device> runQuery(String query) throws DBException {
        List<Device> list = new ArrayList<Device>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Device device = setLogEntry(resultSet);
                list.add(device);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list LogEntrysDAOImpl.runQuery()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    public int getCount() throws DBException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) FROM " + DB_NAME);
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOImpl.getCount()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }


    private Device setLogEntry(ResultSet resultSet) throws SQLException, DBException {
        Device device = new Device();
        device.setId(resultSet.getInt("id"));
        device.setMac(resultSet.getString("mac"));
        device.setName(resultSet.getString("name"));
        device.setDescription(resultSet.getString("descr"));
        device.setSwitchPort(resultSet.getInt("port"));
        device.setPortSpeed(resultSet.getInt("speed"));
        device.setTimeOfRegistration(resultSet.getTimestamp("tor").toLocalDateTime());
        device.setLastSeen(resultSet.getTimestamp("lastseen").toLocalDateTime());
        device.setAccess(resultSet.getInt("access"));

        int switchId = resultSet.getInt("switch_id");
        device.setSwitch(switchDAO.getById(switchId));

        return device;
    }

    private void prepareStatement(Device device, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, device.getId());
        preparedStatement.setString(2, device.getMac());
        preparedStatement.setString(3, device.getName());
        preparedStatement.setString(4, device.getDescription());
        preparedStatement.setInt(5, device.getSwitch().getId());
        preparedStatement.setInt(6, device.getSwitchPort());
        preparedStatement.setInt(7, device.getPortSpeed());
        preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(device.getTimeOfRegistration()));
        preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(device.getLastSeen()));
        preparedStatement.setInt(5, device.getAccess());
    }
}
