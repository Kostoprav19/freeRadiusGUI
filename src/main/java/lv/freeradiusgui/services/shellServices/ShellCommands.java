package lv.freeradiusgui.services.shellServices;

import lv.freeradiusgui.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShellCommands {

    @Autowired AppConfig appConfig;

    public String getStartFreeradius() {
        return appConfig.getProperty("shellStartFreeradius");
    }

    public String getStopFreeradius() {
        return appConfig.getProperty("shellStopFreeradius");
    }

    public String getPgrepFreeradius() {
        return appConfig.getProperty("shellPgrepFreeradius");
    }

    public String getPgrepTomcat() {
        return appConfig.getProperty("shellPgrepTomcat");
    }

    public String getPgrepMysql() {
        return appConfig.getProperty("shellPgrepMysql");
    }
}
