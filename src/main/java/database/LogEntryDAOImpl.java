package database;

import database.jdbc.DAOImpl;
import domain.LogEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniels on 20.10.2015..
 */
public class LogEntryDAOImpl extends DAOImpl implements DAOinterface<LogEntry> {
    public void create(LogEntry logEntry) throws DBException {
        if (logEntry == null) {
            return;
        }

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO auditLogs VALUES (default, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setLong(1, logEntry.getUserId());
            preparedStatement.setString(2, logEntry.getIp());
            preparedStatement.setString(3, logEntry.getCommand());
            preparedStatement.setString(4, logEntry.getValue());
            preparedStatement.setLong(5, logEntry.getAccessObjectID());
            preparedStatement.setDate(6, new java.sql.Date(logEntry.getDate().getTime()));

            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()){
                logEntry.setId(rs.getLong(1));
            }
        } catch (Throwable e) {
            System.out.println("Exception while execute logEntryDAOImpl.create()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public LogEntry getById(Long id) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM auditLogs WHERE id = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            LogEntry logEntry = null;
            if (resultSet.next()) {
                logEntry = new LogEntry();
                logEntry.setId(resultSet.getLong("id"));
                logEntry.setUserId(resultSet.getLong("UserID"));
                logEntry.setIp(resultSet.getString("IP"));
                logEntry.setCommand(resultSet.getString("Command"));
                logEntry.setValue(resultSet.getString("Value"));
                logEntry.setAccessObjectID(resultSet.getLong("AccessObjectID"));
                logEntry.setDate(resultSet.getDate("Date"));
            }
            return logEntry;
        } catch (Throwable e) {
            System.out.println("Exception while execute LogEntryDAOImpl.getById()");
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
                    .prepareStatement("DELETE FROM auditLogs WHERE id = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute LogEntryDAOImpl.delete()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void update(LogEntry logEntry) throws DBException {
        if (logEntry == null) {
            return;
        }

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE auditLogs SET UserID = ?, IP = ?, Command = ?, Value = ?, AccessObjectID = ?, Date = ? WHERE id = ?");
            preparedStatement.setLong(1, logEntry.getUserId());
            preparedStatement.setString(2, logEntry.getIp());
            preparedStatement.setString(3, logEntry.getCommand());
            preparedStatement.setString(4, logEntry.getValue());
            preparedStatement.setLong(5, logEntry.getAccessObjectID());
            preparedStatement.setDate(6, new java.sql.Date(logEntry.getDate().getTime()));
            preparedStatement.setLong(7, logEntry.getId());

            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute LogEntryDAOImpl.update()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<LogEntry> getAll() throws DBException {
        List<LogEntry> products = new ArrayList<LogEntry>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM auditLogs");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LogEntry logEntry = new LogEntry();
                logEntry.setId(resultSet.getLong("id"));
                logEntry.setUserId(resultSet.getLong("UserID"));
                logEntry.setIp(resultSet.getString("IP"));
                logEntry.setCommand(resultSet.getString("Command"));
                logEntry.setValue(resultSet.getString("Value"));
                logEntry.setAccessObjectID(resultSet.getLong("AccessObjectID"));
                logEntry.setDate(resultSet.getDate("Date"));
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list LogEntryDAOImpl.getList()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return products;
    }

    public List<LogEntry> filter(String criteria) throws DBException {
        List<LogEntry> list = new ArrayList<LogEntry>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM auditLogs WHERE " + criteria);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LogEntry logEntry = new LogEntry();
                logEntry.setId(resultSet.getLong("id"));
                logEntry.setUserId(resultSet.getLong("UserID"));
                logEntry.setIp(resultSet.getString("IP"));
                logEntry.setCommand(resultSet.getString("Command"));
                logEntry.setValue(resultSet.getString("Value"));
                logEntry.setAccessObjectID(resultSet.getLong("AccessObjectID"));
                logEntry.setDate(resultSet.getDate("Date"));
                list.add(logEntry);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list LogEntrysDAOImpl.getList()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    public LogEntry getByName(String name) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM auditLogs WHERE name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            LogEntry logEntry = null;
            if (resultSet.next()) {
                logEntry = new LogEntry();
                logEntry.setId(resultSet.getLong("id"));
                logEntry.setUserId(resultSet.getLong("UserID"));
                logEntry.setIp(resultSet.getString("IP"));
                logEntry.setCommand(resultSet.getString("Command"));
                logEntry.setValue(resultSet.getString("Value"));
                logEntry.setAccessObjectID(resultSet.getLong("AccessObjectID"));
                logEntry.setDate(resultSet.getDate("Date"));
            }
            return logEntry;
        } catch (Throwable e) {
            System.out.println("Exception while execute LogEntryDAOImpl.getByLogin()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public int getCount() throws DBException {
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT COUNT(*) FROM auditLogs");
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (Throwable e) {
            System.out.println("Exception while execute LogEntryDAOImpl.getCount()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }
}
