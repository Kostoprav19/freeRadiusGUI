package lv.freeradiusgui.services.serverServices;

import java.time.LocalDateTime;
import java.util.List;
import lv.freeradiusgui.domain.Log;

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
