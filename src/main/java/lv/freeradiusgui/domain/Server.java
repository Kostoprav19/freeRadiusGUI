package lv.freeradiusgui.domain;

import lv.freeradiusgui.services.shellServices.ShellCommands;
import lv.freeradiusgui.services.shellServices.ShellExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Daniels on 03.09.2016..
 */
@Component
public class Server {
    @Autowired
    ShellExecutor shellExecutor;

    public static final boolean SERVER_STATUS_UP = true;
    public static final boolean SERVER_STATUS_DOWN = false;

    public static final String FREERADIUS = "freeradius";
    public static final String TOMCAT = "tomcat";
    public static final String MYSQL = "mysql";

    private Map<String, Boolean> statuses;
    private boolean dbChangesFlag;
    private LocalDateTime lastServiceReboot;

    public Server() {
        statuses = new HashMap<>();
        statuses.put(FREERADIUS, false);
        statuses.put(TOMCAT, false);
        statuses.put(MYSQL, false);
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

    private Integer todayRejectedCount;


    public boolean getStatus(String key) {
        updateStatuses();
        return this.statuses.get(key);
    }

    public void updateStatuses(){
        String result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGRE_FREERADIUS);
        this.statuses.put(FREERADIUS, !result.isEmpty());

        result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGRE_TOMCAT);
        this.statuses.put(TOMCAT, !result.isEmpty());

        result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGRE_MYSQL);
        this.statuses.put(MYSQL, !result.isEmpty());
    }

    public Integer getTodayRejectedCount() {
        return todayRejectedCount;
    }

    public void setTodayRejectedCount(Integer todayRejectedCount) {
        this.todayRejectedCount = todayRejectedCount;
    }

    public boolean restartService(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_STOP_FREERADIUS);
        shellExecutor.executeCommand(ShellCommands.COMMAND_START_FREERADIUS);
        updateStatuses();
        if (getStatus(FREERADIUS) == SERVER_STATUS_UP) {
            this.lastServiceReboot = LocalDateTime.now();
            return true;
        } else
            return false;
    }

    public boolean startService(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_START_FREERADIUS);
        updateStatuses();
        if (getStatus(FREERADIUS) == SERVER_STATUS_UP) {
            this.lastServiceReboot = LocalDateTime.now();
            return true;
        } else
            return false;
    }

    public boolean stopService(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_STOP_FREERADIUS);
        updateStatuses();
        if (getStatus(FREERADIUS) == SERVER_STATUS_DOWN) {
            return true;
        } else
            return false;
    }

    public String runCommand(String command){
        return shellExecutor.executeCommand(command);
    }
}
