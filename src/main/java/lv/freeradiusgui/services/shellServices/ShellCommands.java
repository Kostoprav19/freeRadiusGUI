package lv.freeradiusgui.services.shellServices;

/**
 * Created by Daniels on 05.09.2016..
 */
public final class ShellCommands {

    //ps aux| grep free | wc -l //count
    //pgrep -fl free //pid
    public static final String COMMAND_PGRE_FREERADIUS = "pgrep -fl freeradius";

    public static final String COMMAND_PGRE_TOMCAT = "pgrep -fl tomcat";

    public static final String COMMAND_PGRE_MYSQL = "pgrep -fl mysqld";

    public static final String COMMAND_STOP_FREERADIUS = "killall freeradius";

    public static final String COMMAND_START_FREERADIUS = "freeradius";

}
