package lv.freeradiusgui.database.jdbc.sqlite;

import lv.freeradiusgui.database.DBException;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Scanner;

public class DAOImpl {

    private static final String DB_SCRIPT_FILE = "databaseCreationScriptSQLite.sql";
    public static final String DB_FILE_NAME = "database.db";

    public DAOImpl() {
        registerJDBCDriver();
        initDatabase();
    }

    private void registerJDBCDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Exception while registering JDBC driver!");
            e.printStackTrace();
        }
    }

    private void initDatabase() {
        Scanner s = null;
        try {
            s = new Scanner(new File(DB_SCRIPT_FILE));
        } catch (FileNotFoundException e) {
            System.out.println("File " + DB_SCRIPT_FILE + " not found!");
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        while (s.hasNextLine()){
            sb.append(s.nextLine() + "\n");
        }
        sb.delete(sb.length() - 1, sb.length());
        s.close();
        String databaseCreationScript = sb.toString();

        runQuery(databaseCreationScript);

    }

    private void runQuery(String query){
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();
        } catch (Throwable e) {
            System.out.println("Exception while execute DAOImpl.runQuery()");
            e.printStackTrace();
        } finally {
            try {
                closeConnection(connection);
            } catch (DBException e) {
                e.printStackTrace();
            }
        }
    }

    protected Connection getConnection() throws DBException {
        try{
            return DriverManager.getConnection("jdbc:sqlite:" + DB_FILE_NAME);
        } catch (SQLException e) {
            System.out.println("Exception while connecting to lv.freeradiusgui.config.database");
            e.printStackTrace();
            throw new DBException(e);
        }
    }

    protected void closeConnection(Connection connection) throws DBException {
        try {
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Exception while closing connection to lv.freeradiusgui.config.database");
            e.printStackTrace();
            throw new DBException(e);
        }
    }

}
