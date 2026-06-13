package lv.freeradiusgui.services.shellServices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lv.freeradiusgui.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ShellCommandsTest {

    @Test
    public void getStartFreeradius_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals("freeradius", shellCommands.getStartFreeradius());
    }

    @Test
    public void getStopFreeradius_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals("killall freeradius", shellCommands.getStopFreeradius());
    }

    @Test
    public void getPgrepFreeradius_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals("pgrep -fl freeradius", shellCommands.getPgrepFreeradius());
    }

    @Test
    public void getPgrepTomcat_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals("pgrep -fl tomcat", shellCommands.getPgrepTomcat());
    }

    @Test
    public void getPgrepMysql_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals("pgrep -fl mysqld", shellCommands.getPgrepMysql());
    }

    private ShellCommands shellCommandsWith(AppConfig appConfig) {
        var shellCommands = new ShellCommands();
        ReflectionTestUtils.setField(shellCommands, "appConfig", appConfig);
        return shellCommands;
    }
}
