package lv.freeradiusgui.utils;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Dan on 08.07.2016.
 */
@Service
public class AppConfig {

    public static final String PROPERTIES_FILE = "config.properties";
    private final Properties configProp = new Properties();

    private String PathToUsersFile;
    private String PathToClientsConfFile;
    private String PathToLogDirectory;

    public String getPathToUsersFile() {
        return PathToUsersFile;
    }

    public String getPathToClientsConfFile() {
        return PathToClientsConfFile;
    }

    public String getPathToLogDirectory() {
        return PathToLogDirectory;
    }
    public AppConfig() {
        super();
        System.err.println("Reading properties from file '" + PROPERTIES_FILE + "'");

        readPropertyFile();
        PathToClientsConfFile = getProperty("clientsfilepath");
        PathToUsersFile = getProperty("usersfilepath");
        PathToLogDirectory = getProperty("logfilesdirpath");

        System.err.println("clientsfilepath = " + getPathToClientsConfFile());
        System.err.println("usersfilepath = " + getPathToUsersFile());
        System.err.println("logfilesdirpath = " + getPathToLogDirectory());
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
