package lv.freeradiusgui.services.shellServices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lv.freeradiusgui.config.AppConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ShellCommandsTest {

    private static final String SHELL_START_FREERADIUS = "SHELL_START_FREERADIUS";
    private static final String SHELL_STOP_FREERADIUS = "SHELL_STOP_FREERADIUS";
    private static final String SHELL_PGREP_FREERADIUS = "SHELL_PGREP_FREERADIUS";
    private static final String SHELL_PGREP_TOMCAT = "SHELL_PGREP_TOMCAT";
    private static final String SHELL_PGREP_MYSQL = "SHELL_PGREP_MYSQL";

    @AfterEach
    public void clearSystemProperties() {
        System.clearProperty(SHELL_START_FREERADIUS);
        System.clearProperty(SHELL_STOP_FREERADIUS);
        System.clearProperty(SHELL_PGREP_FREERADIUS);
        System.clearProperty(SHELL_PGREP_TOMCAT);
        System.clearProperty(SHELL_PGREP_MYSQL);
    }

    @Test
    public void getStartFreeradius_readsResolvedValueFromAppConfig() {
        System.setProperty(SHELL_START_FREERADIUS, "start-command");
        var shellCommands = shellCommandsWith(new AppConfig());

        assertEquals("start-command", shellCommands.getStartFreeradius());
    }

    @Test
    public void getStopFreeradius_readsResolvedValueFromAppConfig() {
        System.setProperty(SHELL_STOP_FREERADIUS, "stop-command");
        var shellCommands = shellCommandsWith(new AppConfig());

        assertEquals("stop-command", shellCommands.getStopFreeradius());
    }

    @Test
    public void getPgrepFreeradius_readsResolvedValueFromAppConfig() {
        System.setProperty(SHELL_PGREP_FREERADIUS, "pgrep-freeradius");
        var shellCommands = shellCommandsWith(new AppConfig());

        assertEquals("pgrep-freeradius", shellCommands.getPgrepFreeradius());
    }

    @Test
    public void getPgrepTomcat_readsResolvedValueFromAppConfig() {
        System.setProperty(SHELL_PGREP_TOMCAT, "pgrep-tomcat");
        var shellCommands = shellCommandsWith(new AppConfig());

        assertEquals("pgrep-tomcat", shellCommands.getPgrepTomcat());
    }

    @Test
    public void getPgrepMysql_readsResolvedValueFromAppConfig() {
        System.setProperty(SHELL_PGREP_MYSQL, "pgrep-mysql");
        var shellCommands = shellCommandsWith(new AppConfig());

        assertEquals("pgrep-mysql", shellCommands.getPgrepMysql());
    }

    @Test
    public void getStartFreeradius_usesSystemPropertyOverrideThroughAppConfigResolver() {
        System.setProperty(SHELL_START_FREERADIUS, "override-start-command");
        var shellCommands = shellCommandsWith(new AppConfig());

        assertEquals("override-start-command", shellCommands.getStartFreeradius());
    }

    private ShellCommands shellCommandsWith(AppConfig appConfig) {
        var shellCommands = new ShellCommands();
        ReflectionTestUtils.setField(shellCommands, "appConfig", appConfig);
        return shellCommands;
    }
}
