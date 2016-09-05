package lv.freeradiusgui.domain;

import lv.freeradiusgui.services.shellServices.ShellCommands;
import lv.freeradiusgui.services.shellServices.ShellExecutor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Daniels on 03.09.2016..
 */
public class Server {
    @Autowired
    ShellExecutor shellExecutor;

    public boolean isUp() {
        String result = shellExecutor.executeCommand(ShellCommands.COMMAND_PGREP);
        return !result.isEmpty();
    }


}
