package lv.freeradiusgui.services.shellServices;

/**
 * Created by Daniels on 05.09.2016..
 */
public final class ShellCommands {

    //ps aux| grep free | wc -l //count
    //pgrep -fl free //pid
    public static final String COMMAND_PGREP = "pgrep -fl freeradius";

    public static final String COMMAND_STOP_SERVER = "killall freeradius";

    public static final String COMMAND_START_SERVER = "freeradius";

}
