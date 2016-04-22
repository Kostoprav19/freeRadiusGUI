package lv.freeradiusgui.database.jdbc.sqlite;

import lv.freeradiusgui.database.DBException;
import lv.freeradiusgui.database.SwitchDAOInterface;
import lv.freeradiusgui.domain.Switch;
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
public class SwitchDAOImpl extends DAOImpl implements SwitchDAOInterface {

    public static final String DB_NAME = "switches";
    public static final String DB_FIELDS = "id = ?, mac = ?, name = ?, descr = ?, ip = ?";

    public void create(Switch aSwitch) throws DBException {
        if (aSwitch == null) {
            return;
        }

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO " + DB_NAME + " VALUES (default, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            prepareStatement(aSwitch, preparedStatement);

            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()){
                aSwitch.setId(rs.getInt(1));
            }
        } catch (Throwable e) {
            System.out.println("Exception while execute SwitchDAOImpl.create()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public Switch getById(Integer id) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM " + DB_NAME + " WHERE LogID = ?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Switch aSwitch = null;
            if (resultSet.next()) {
                aSwitch = setLogEntry(resultSet);
            }
            return aSwitch;
        } catch (Throwable e) {
            System.out.println("Exception while execute SwitchDAOImpl.getById()");
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
            System.out.println("Exception while execute SwitchDAOImpl.delete()");
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
            System.out.println("Exception while execute SwitchDAOImpl.deleteAll()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void update(Switch aSwitch) throws DBException {
        if (aSwitch == null) {
            return;
        }
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE " + DB_NAME + " SET " + DB_FIELDS + " WHERE LogID = ?");
            prepareStatement(aSwitch, preparedStatement);
            preparedStatement.setInt(6, aSwitch.getId());

            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute SwitchDAOImpl.update()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<Switch> getAll(boolean sortOrder) throws DBException {
        List<Switch> list = new ArrayList<Switch>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + DB_NAME);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Switch aSwitch = setLogEntry(resultSet);
                list.add(aSwitch);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list SwitchDAOImpl.getAll()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    private List<Switch> filter(String criteria) throws DBException {
        List<Switch> list = new ArrayList<Switch>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + DB_NAME + " WHERE " + criteria);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Switch aSwitch = setLogEntry(resultSet);
                list.add(aSwitch);
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

    private List<Switch> runQuery(String query) throws DBException {
        List<Switch> list = new ArrayList<Switch>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Switch aSwitch = setLogEntry(resultSet);
                list.add(aSwitch);
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
            System.out.println("Exception while execute SwitchDAOImpl.getCount()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    private Switch setLogEntry(ResultSet resultSet) throws SQLException {
        Switch aSwitch = new Switch();
        aSwitch.setId(resultSet.getInt("id"));
        aSwitch.setMac(resultSet.getString("mac"));
        aSwitch.setName(resultSet.getString("name"));
        aSwitch.setDescription(resultSet.getString("descr"));
        aSwitch.setIp(resultSet.getString("ip"));
        return aSwitch;
    }

    private void prepareStatement(Switch aSwitch, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, aSwitch.getId());
        preparedStatement.setString(2, aSwitch.getMac());
        preparedStatement.setString(3, aSwitch.getName());
        preparedStatement.setString(4, aSwitch.getDescription());
        preparedStatement.setString(5, aSwitch.getIp());
    }
}
