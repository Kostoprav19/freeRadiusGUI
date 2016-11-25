package lv.freeradiusgui.services.serverServices;

import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.mailServices.MailService;
import lv.freeradiusgui.services.shellServices.ShellCommands;
import lv.freeradiusgui.services.shellServices.ShellExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Daniels on 24.11.2016..
 */
@Service
public class ServerServiceImpl implements ServerService {

    @Autowired
    ShellExecutor shellExecutor;
    
    @Autowired
    Server server;

    @Autowired
    MailService mailService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void updateStatuses(){
        String result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGRE_FREERADIUS);
        server.setStatus(Server.FREERADIUS, !result.isEmpty());

        result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGRE_TOMCAT);
        server.setStatus(Server.TOMCAT, !result.isEmpty());

        result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGRE_MYSQL);
        server.setStatus(Server.MYSQL, !result.isEmpty());

        logger.info("Freeradius status: " + (server.getStatus(Server.FREERADIUS) ? "UP" : "DOWN") );
        logger.info("Tomcat status: " + (server.getStatus(Server.TOMCAT) ? "UP" : "DOWN") );
        logger.info("Mysql status: " + (server.getStatus(Server.MYSQL) ? "UP" : "DOWN") );
    }

    public Integer getTodayRejectedCount() {
        return server.getRejectedLogsListToday().size();
    }

    public boolean restartFreeradius(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_STOP_FREERADIUS);
        shellExecutor.executeCommand(ShellCommands.COMMAND_START_FREERADIUS);
        updateStatuses();
        if (server.getStatus(Server.FREERADIUS) == Server.SERVER_STATUS_UP) {
            server.setLastServiceReboot(LocalDateTime.now());
            return true;
        } else
            return false;
    }

    public boolean startFreeradius(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_START_FREERADIUS);
        updateStatuses();
        if (server.getStatus(server.FREERADIUS) == server.SERVER_STATUS_UP) {
            server.setLastServiceReboot(LocalDateTime.now());
            return true;
        } else
            return false;
    }

    public boolean stopFreeradius(){
        shellExecutor.executeCommand(ShellCommands.COMMAND_STOP_FREERADIUS);
        updateStatuses();
        if (server.getStatus(server.FREERADIUS) == server.SERVER_STATUS_DOWN) {
            return true;
        } else
            return false;
    }

    public String runCommand(String command){
        return shellExecutor.executeCommand(command);
    }

    @Override
    public boolean getStatus(String key) {
        return server.getStatus(key);
    }

    @Override
    public LocalDateTime getLastServiceReboot() {
        return server.getLastServiceReboot();
    }

    @Override
    public void setDbChangesFlag() {
        server.setDbChangesFlag();
    }

    @Override
    public void unsetDbChangesFlag() {
        server.unsetDbChangesFlag();
    }

    @Override
    public boolean getDbgChangesFlag() {
        return server.getDbgChangesFlag();
    }

    public boolean setTodayRejected(List<Log> logList) {
        int delta = logList.size() - getTodayRejectedCount();
        server.setTodayRejected(logList);
        server.setRejectedLogsCounter(delta);
        if ( delta > 0) mailService.sendMail();
        return (delta > 0);
    }

    @Override
    public int getRejectedLogsCounter() {
        return server.getRejectedLogsCounter();
    }

    @Override
    public int getRejectedLogsTodayCounter() {
        return server.getRejectedLogsListToday().size();
    }

    @Override
    public List<Log> getRejectedLogsListToday() {
        return server.getRejectedLogsListToday();
    }
}
