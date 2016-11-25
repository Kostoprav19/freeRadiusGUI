package lv.freeradiusgui.domain;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Daniels on 03.09.2016..
 */
@Component
public class Server {

    public static final boolean SERVER_STATUS_UP = true;
    public static final boolean SERVER_STATUS_DOWN = false;

    public static final String FREERADIUS = "freeradius";
    public static final String TOMCAT = "tomcat";
    public static final String MYSQL = "mysql";

    private Map<String, Boolean> statuses;
    private boolean dbChangesFlag;
    private LocalDateTime lastServiceReboot;
    private List<Log> rejectedLogsListToday;
    private int rejectedLogsCounter;

    public Server() {
        statuses = new HashMap<>();
        statuses.put(FREERADIUS, false);
        statuses.put(TOMCAT, false);
        statuses.put(MYSQL, false);
        this.rejectedLogsListToday = new ArrayList<>();
        this.lastServiceReboot = null;
        this.dbChangesFlag = false;
        this.rejectedLogsCounter = 0;
    }

    public void setDbChangesFlag() {
        this.dbChangesFlag = true;
    }

    public void unsetDbChangesFlag() {
        this.dbChangesFlag = false;
    }

    public boolean getDbgChangesFlag() {
        return this.dbChangesFlag;
    }

    public LocalDateTime getLastServiceReboot() {
        return lastServiceReboot;
    }

    public void setLastServiceReboot(LocalDateTime lastServiceReboot) {
        this.lastServiceReboot = lastServiceReboot;
    }

    public boolean getStatus(String key) {
        return this.statuses.get(key);
    }

    public void setStatus(String key, Boolean value) {
        this.statuses.put(key, value);
    }

    public void setTodayRejected(List<Log> logList) {
        this.rejectedLogsListToday = logList;
    }

    public List<Log> getRejectedLogsListToday() {
        return rejectedLogsListToday;
    }

    public int getRejectedLogsCounter() {
        return rejectedLogsCounter;
    }

    public void setRejectedLogsCounter(int rejectedLogsCounter) {
        this.rejectedLogsCounter = rejectedLogsCounter;
    }
}
