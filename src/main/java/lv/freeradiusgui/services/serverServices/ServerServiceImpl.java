package lv.freeradiusgui.services.serverServices;

import java.time.LocalDateTime;
import java.util.List;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.mailServices.MailService;
import lv.freeradiusgui.services.shellServices.ShellCommands;
import lv.freeradiusgui.services.shellServices.ShellExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerServiceImpl implements ServerService {

    @Autowired ShellExecutor shellExecutor;
    @Autowired ShellCommands shellCommands;

    @Autowired Server server;

    @Autowired MailService mailService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void updateStatuses() {
        String result = shellExecutor.executeCommand(shellCommands.getPgrepFreeradius());
        server.setStatus(Server.FREERADIUS, !result.isEmpty());

        result = shellExecutor.executeCommand(shellCommands.getPgrepTomcat());
        server.setStatus(Server.TOMCAT, !result.isEmpty());

        result = shellExecutor.executeCommand(shellCommands.getPgrepMysql());
        server.setStatus(Server.MYSQL, !result.isEmpty());

        logger.info("Freeradius status: " + (server.getStatus(Server.FREERADIUS) ? "UP" : "DOWN"));
        logger.info("Tomcat status: " + (server.getStatus(Server.TOMCAT) ? "UP" : "DOWN"));
        logger.info("Mysql status: " + (server.getStatus(Server.MYSQL) ? "UP" : "DOWN"));
    }

    public Integer getTodayRejectedCount() {
        return server.getRejectedLogsListToday().size();
    }

    public boolean restartFreeradius() {
        shellExecutor.executeCommand(shellCommands.getStopFreeradius());
        shellExecutor.executeCommand(shellCommands.getStartFreeradius());
        updateStatuses();
        if (server.getStatus(Server.FREERADIUS) == Server.SERVER_STATUS_UP) {
            server.setLastServiceReboot(LocalDateTime.now());
            return true;
        } else return false;
    }

    public boolean startFreeradius() {
        shellExecutor.executeCommand(shellCommands.getStartFreeradius());
        updateStatuses();
        if (server.getStatus(server.FREERADIUS) == server.SERVER_STATUS_UP) {
            server.setLastServiceReboot(LocalDateTime.now());
            return true;
        } else return false;
    }

    public boolean stopFreeradius() {
        shellExecutor.executeCommand(shellCommands.getStopFreeradius());
        updateStatuses();
        if (server.getStatus(server.FREERADIUS) == server.SERVER_STATUS_DOWN) {
            return true;
        } else return false;
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
        if (delta > 0) mailService.sendMail();
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
