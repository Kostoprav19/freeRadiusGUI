package database;

import domain.AccessObject;
import database.jdbc.mysql.DAOImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniels on 20.10.2015..
 */
public class AccessObjectDAOImpl extends DAOImpl implements DAOinterface<AccessObject>{
    public void create(AccessObject accessObject) throws DBException {
        if (accessObject == null) {
            return;
        }

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO accessObjects VALUES (default, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, accessObject.getName());
            preparedStatement.setString(2, accessObject.getDescription());
            preparedStatement.setString(3, accessObject.getPlatform());
            preparedStatement.setString(4, accessObject.getUrl());

            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()){
                accessObject.setId(rs.getLong(1));
            }
        } catch (Throwable e) {
            System.out.println("Exception while execute accessObjectDAOImpl.create()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public AccessObject getById(Long id) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM accessObjects WHERE AccessObjectID = ?");
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            AccessObject accessObject = null;
            if (resultSet.next()) {
                accessObject = new AccessObject();
                accessObject.setId(resultSet.getLong("AccessObjectID"));
                accessObject.setName(resultSet.getString("name"));
                accessObject.setDescription(resultSet.getString("Description"));
                accessObject.setPlatform(resultSet.getString("Platform"));
                accessObject.setUrl(resultSet.getString("URL"));
            }
            return accessObject;
        } catch (Throwable e) {
            System.out.println("Exception while execute AccessObjectDAOImpl.getById()");
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
                    .prepareStatement("DELETE FROM accessObjects WHERE AccessObjectID = ?");
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute AccessObjectDAOImpl.delete()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public void update(AccessObject accessObject) throws DBException {
        if (accessObject == null) {
            return;
        }

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("UPDATE accessObjects SET name = ?, Description = ?, Platform = ?, URL = ? WHERE AccessObjectID = ?");
            preparedStatement.setString(1, accessObject.getName());
            preparedStatement.setString(2, accessObject.getDescription());
            preparedStatement.setString(3, accessObject.getPlatform());
            preparedStatement.setString(4, accessObject.getUrl());
            preparedStatement.setLong(5, accessObject.getId());

            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute AccessObjectDAOImpl.update()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<AccessObject> getAll() throws DBException {
        List<AccessObject> products = new ArrayList<AccessObject>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accessObjects");

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AccessObject accessObject = new AccessObject();
                accessObject.setId(resultSet.getLong("AccessObjectID"));
                accessObject.setName(resultSet.getString("name"));
                accessObject.setDescription(resultSet.getString("Description"));
                accessObject.setPlatform(resultSet.getString("Platform"));
                accessObject.setUrl(resultSet.getString("URL"));
                products.add(accessObject);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list AccessObjectDAOImpl.getList()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return products;
    }

    public List<AccessObject> filter(String criteria) throws DBException {
        List<AccessObject> list = new ArrayList<AccessObject>();
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accessObjects WHERE " + criteria);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                AccessObject accessObject = new AccessObject();
                accessObject.setId(resultSet.getLong("AccessObjectID"));
                accessObject.setName(resultSet.getString("name"));
                accessObject.setDescription(resultSet.getString("Description"));
                accessObject.setPlatform(resultSet.getString("Platform"));
                accessObject.setUrl(resultSet.getString("URL"));
                list.add(accessObject);
            }
        } catch (Throwable e) {
            System.out.println("Exception while getting customer list AccessObjectsDAOImpl.getList()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
        return list;
    }

    public AccessObject getByName(String name) throws DBException {
        Connection connection = null;

        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection
                    .prepareStatement("SELECT * FROM accessObjects WHERE name = ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            AccessObject accessObject = null;
            if (resultSet.next()) {
                accessObject = new AccessObject();
                accessObject.setId(resultSet.getLong("AccessObjectID"));
                accessObject.setName(resultSet.getString("name"));
                accessObject.setDescription(resultSet.getString("Description"));
                accessObject.setPlatform(resultSet.getString("Platform"));
                accessObject.setUrl(resultSet.getString("URL"));
            }
            return accessObject;
        } catch (Throwable e) {
            System.out.println("Exception while execute AccessObjectDAOImpl.getByLogin()");
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
                    .prepareStatement("SELECT COUNT(*) FROM accessObjects");
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (Throwable e) {
            System.out.println("Exception while execute AccessObjectsDAOImpl.getCount()");
            e.printStackTrace();
            throw new DBException(e);
        } finally {
            closeConnection(connection);
        }
    }
}
