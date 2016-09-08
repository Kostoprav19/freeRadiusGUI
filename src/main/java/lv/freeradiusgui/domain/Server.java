package lv.freeradiusgui.domain;

import lv.freeradiusgui.services.shellServices.ShellCommands;
import lv.freeradiusgui.services.shellServices.ShellExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Daniels on 03.09.2016..
 */
@Component
public class Server {
    @Autowired
    ShellExecutor shellExecutor;

    public static final boolean SERVER_STATUS_UP = true;
    public static final boolean SERVER_STATUS_DOWN = false;

    private boolean status;
    private boolean changes;
    private LocalDateTime lastServiceReboot;

    public boolean isChanges() {
        return changes;
    }

    public void setChanges(boolean changes) {
        this.changes = changes;
    }

    public LocalDateTime getLastServiceReboot() {
        return lastServiceReboot;
    }

    public void setLastServiceReboot(LocalDateTime lastServiceReboot) {
        this.lastServiceReboot = lastServiceReboot;
    }

    public Server() {
        this.changes = false;
    }

    private Integer todayRejectedCount;


    public boolean getStatus() {
        updateStatus();
        return this.status;
    }

    public void updateStatus(){
        String result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGREP);
        this.status = !result.isEmpty();
    }

    public Integer getTodayRejectedCount() {
        return todayRejectedCount;
    }

    public void setTodayRejectedCount(Integer todayRejectedCount) {
        this.todayRejectedCount = todayRejectedCount;
    }

    public boolean restartService(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_STOP_SERVER);
        shellExecutor.executeCommand(ShellCommands.COMMAND_START_SERVER);
        updateStatus();
        if (getStatus() == SERVER_STATUS_UP) {
            this.lastServiceReboot = LocalDateTime.now();
            return true;
        } else
            return false;
    }

    public boolean startService(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_START_SERVER);
        updateStatus();
        if (getStatus() == SERVER_STATUS_UP) {
            this.lastServiceReboot = LocalDateTime.now();
            return true;
        } else
            return false;
    }

    public boolean stopService(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_STOP_SERVER);
        updateStatus();
        if (getStatus() == SERVER_STATUS_DOWN) {
            return true;
        } else
            return false;
    }
}
