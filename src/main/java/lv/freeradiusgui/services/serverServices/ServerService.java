package lv.freeradiusgui.services.serverServices;

import lv.freeradiusgui.domain.Log;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Daniels on 24.11.2016..
 */
public interface ServerService {

    void updateStatuses();

    boolean restartFreeradius();

    boolean startFreeradius();

    boolean stopFreeradius();

    String runCommand(String command);

    boolean getStatus(String key);

    LocalDateTime getLastServiceReboot();

    void setDbChangesFlag();

    void unsetDbChangesFlag();

    boolean getDbgChangesFlag();

    boolean setTodayRejected(List<Log> logList);

    int getRejectedLogsCounter();

    int getRejectedLogsTodayCounter();

    List<Log> getRejectedLogsListToday();
}