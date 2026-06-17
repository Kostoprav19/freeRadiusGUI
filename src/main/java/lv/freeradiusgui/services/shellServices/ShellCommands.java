package lv.freeradiusgui.services.shellServices;

import lv.freeradiusgui.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShellCommands {

    @Autowired AppConfig appConfig;

    public String getRestartFreeradius() {
        return appConfig.getProperty("shellRestartFreeradius");
    }

    public String getPgrepFreeradius() {
        return appConfig.getProperty("shellPgrepFreeradius");
    }

    public String getPgrepMysql() {
        return appConfig.getProperty("shellPgrepMysql");
    }
}
