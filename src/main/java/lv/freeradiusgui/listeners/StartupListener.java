package lv.freeradiusgui.listeners;

import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.DeviceService;
import lv.freeradiusgui.services.LogService;
import lv.freeradiusgui.services.SwitchService;
import lv.freeradiusgui.services.filesServices.FileOperationResult;
import lv.freeradiusgui.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Dan on 17.08.2016.
 */
@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SwitchService switchService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    LogService logService;
    @Autowired
    AppConfig appConfig;
    @Autowired
    Server server;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        logger.info("---------------- Starting Application ----------------");
        logger.info("Loading files:");
        if (switchService.reloadFromConfig())
            logger.info(appConfig.getPathToClientsConfFile() + " - OK");
        else
            logger.info(appConfig.getPathToClientsConfFile() + " - FAIL");

        if (deviceService.reloadFromConfig())
            logger.info(appConfig.getPathToUsersFile() + " - OK");
        else
            logger.info(appConfig.getPathToUsersFile() + " - FAIL");

        LocalDateTime date = LocalDateTime.now();
        FileOperationResult result = logService.loadFromFile(date);
        if (result.ok)
            logger.info("Log file name '" + result.message + "' - OK");
        else
            logger.info("Log file - FAIL - " + result.message);

        server.updateStatuses();
        logger.info("Freeradius status: " + (server.getStatus(Server.FREERADIUS) ? "UP" : "DOWN") );
        logger.info("Tomcat status: " + (server.getStatus(Server.TOMCAT) ? "UP" : "DOWN") );
        logger.info("Mysql status: " + (server.getStatus(Server.MYSQL) ? "UP" : "DOWN") );

        server.setTodayRejectedCount(logService.countRejected(logService.getByDate(LocalDateTime.now())));
        logger.info("Today rejected count: " + server.getTodayRejectedCount());

        logger.info("---------------- Application started----------------");
    }


}
