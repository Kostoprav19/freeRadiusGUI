package lv.freeradiusgui.services.shellServices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lv.freeradiusgui.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ShellCommandsTest {

    @Test
    public void getRestartFreeradius_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals(
                "curl -s -X POST --unix-socket /var/run/docker.sock"
                        + " http://localhost/containers/freeradiusgui-radius/restart",
                shellCommands.getRestartFreeradius());
    }

    @Test
    public void getPgrepFreeradius_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals(
                "curl -s --unix-socket /var/run/docker.sock"
                        + " http://localhost/containers/freeradiusgui-radius/json | grep -oE"
                        + " '\"Running\": *true'",
                shellCommands.getPgrepFreeradius());
    }

    @Test
    public void getPgrepMysql_readsValueFromAppConfig() {
        var shellCommands = shellCommandsWith(new AppConfig());
        assertEquals(
                "curl -s --unix-socket /var/run/docker.sock"
                        + " http://localhost/containers/freeradiusgui-db/json | grep -oE"
                        + " '\"Running\": *true'",
                shellCommands.getPgrepMysql());
    }

    private ShellCommands shellCommandsWith(AppConfig appConfig) {
        var shellCommands = new ShellCommands();
        ReflectionTestUtils.setField(shellCommands, "appConfig", appConfig);
        return shellCommands;
    }
}
