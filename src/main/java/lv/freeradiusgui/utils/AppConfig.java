package lv.freeradiusgui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Dan on 08.07.2016.
 */
@Service
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String PROPERTIES_FILE = "config.properties";
    private final Properties configProp = new Properties();

    private String pathToUsersFile;
    private String pathToClientsConfFile;
    private String pathToLogDirectory;
    private String dbDriverClass;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private int dbMinPoolSize;
    private int dbMaxPoolSize;
    private int dbCheckoutTimeout;
    private int dbMaxStatements;
    private int dbIdleConnectionTestPeriod;


    public String getPathToUsersFile() {
        return pathToUsersFile;
    }

    public String getPathToClientsConfFile() {
        return pathToClientsConfFile;
    }

    public String getPathToLogDirectory() {
        return pathToLogDirectory;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbDriverClass() {
        return dbDriverClass;
    }

    public int getDbMinPoolSize() {
        return dbMinPoolSize;
    }

    public int getDbMaxPoolSize() {
        return dbMaxPoolSize;
    }

    public int getDbCheckoutTimeout() {
        return dbCheckoutTimeout;
    }

    public int getDbMaxStatements() {
        return dbMaxStatements;
    }

    public int getDbIdleConnectionTestPeriod() {
        return dbIdleConnectionTestPeriod;
    }

    public AppConfig() {
        super();
        logger.info("Reading properties from file '" + PROPERTIES_FILE + "'");

        setVariables();

        logger.info("setting variables:");
        logger.info("clientsfilepath = " + getPathToClientsConfFile());
        logger.info("usersfilepath = " + getPathToUsersFile());
        logger.info("logfilesdirpath = " + getPathToLogDirectory());
        logger.info("Database connection settings:");
        logger.info("jdbcUrl = " + getDbUrl());
        logger.info("dbDriverClass = " + getDbDriverClass());
        logger.info("dbUser = " + getDbUser());
        logger.info("dbMinPoolSize = " + getDbMinPoolSize());
        logger.info("dbMaxPoolSize = " + getDbMaxPoolSize());
        logger.info("dbCheckoutTimeout = " + getDbCheckoutTimeout());
        logger.info("dbMaxStatements = " + getDbMaxStatements());
        logger.info("dbIdleConnectionTestPeriod = " + getDbIdleConnectionTestPeriod());
    }

    private void setVariables() {
        readPropertyFile();
        //PATHES
        pathToClientsConfFile = getProperty("clientsfilepath");
        pathToUsersFile = getProperty("usersfilepath");
        pathToLogDirectory = getProperty("logfilesdirpath");
        //DATABASE
        dbDriverClass = getProperty("dbDriverClass");
        dbUrl = getProperty("dbUrl");
        dbUser = getProperty("dbUser");
        dbPassword = getProperty("dbPassword");
        dbMinPoolSize = Integer.parseInt(getProperty("dbMinPoolSize"));
        dbMaxPoolSize = Integer.parseInt(getProperty("dbMaxPoolSize"));
        dbCheckoutTimeout = Integer.parseInt(getProperty("dbCheckoutTimeout"));
        dbMaxStatements = Integer.parseInt(getProperty("dbMaxStatements"));
        dbIdleConnectionTestPeriod = Integer.parseInt(getProperty("dbIdleConnectionTestPeriod"));
    }

    public String getProperty(String key){
        return configProp.getProperty(key);
    }

    private void readPropertyFile() {
        {
            //Private constructor to restrict new instances
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
            try {
                configProp.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
