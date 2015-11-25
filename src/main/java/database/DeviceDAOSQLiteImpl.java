package database;

import database.jdbc.sqlite.DAOImpl;
import domain.Device;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */
@Component
public class DeviceDAOSQLiteImpl extends DAOImpl implements DeviceDAOInterface {

    public static final String DB_NAME = "devices";

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
            System.out.println("Exception while execute logEntryDAOImpl.create()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public Device getById(Long id) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM " + DB_NAME + " WHERE LogID = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Device device = null;
            if (resultSet.next()) {
                device = setLogEntry(resultSet);
            }
            return device;
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOSQLiteImpl.getById()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void delete(Long id) throws DBException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("DELETE FROM " + DB_NAME + " WHERE LogID = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOSQLiteImpl.delete()");
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
            System.out.println("Exception while execute DeviceDAOSQLiteImpl.deleteAll()");
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
                    .prepareStatement("UPDATE " + DB_NAME + " SET UserID = ?, IP = ?, Command = ?, Value = ?, Date = ? WHERE LogID = ?");
            prepareStatement(device, preparedStatement);
            preparedStatement.setLong(6, device.getId());

            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOSQLiteImpl.update()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<Device> getAll(boolean sortOrder) throws DBException {
        List<Device> list = new ArrayList<Device>();
        Connection connection = null;
        String sortStr;
        if (sortOrder == SORT_ASCENDING) sortStr = "ASC"; else sortStr = "DESC";
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + DB_NAME + " ORDER BY Date " + sortStr);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Device device = setLogEntry(resultSet);
                list.add(device);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list DeviceDAOSQLiteImpl.getAll()");
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
                    .prepareStatement("SELECT COUNT(*) FROM " + DB_NAME + "");
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (Throwable e) {
            System.out.println("Exception while execute DeviceDAOSQLiteImpl.getCount()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }


    private Device setLogEntry(ResultSet resultSet) throws SQLException {
        Device device = new Device();
        device.setId(resultSet.getInt("id"));
        device.setMac(resultSet.getString("mac"));
        device.setName(resultSet.getString("name"));
        device.setDescription(resultSet.getString("descr"));
        device.setOnSwitch(resultSet.getInt("switch"));
        device.setOnPort(resultSet.getInt("port"));
        device.setSpeed(resultSet.getInt("speed"));
        device.setTimeOfRegistration(resultSet.getTimestamp("tor").toLocalDateTime());
        device.setLastSeen(resultSet.getTimestamp("lastseen").toLocalDateTime());
        device.setAccess(resultSet.getInt("access"));
        return device;
    }

    private void prepareStatement(Device device, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1, device.getId());
        preparedStatement.setString(2, device.getMac());
        preparedStatement.setString(3, device.getName());
        preparedStatement.setString(4, device.getDescription());
        preparedStatement.setInt(5, device.getOnSwitch());
        preparedStatement.setInt(6, device.getOnPort());
        preparedStatement.setInt(7, device.getSpeed());
        preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(device.getTimeOfRegistration()));
        preparedStatement.setTimestamp(8, java.sql.Timestamp.valueOf(device.getLastSeen()));
        preparedStatement.setInt(5, device.getAccess());
    }
}
