package lv.freeradiusgui.config;

import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.services.DeviceService;
import lv.freeradiusgui.services.LogService;
import lv.freeradiusgui.services.SwitchService;
import lv.freeradiusgui.services.filesServices.FileOperationResult;
import lv.freeradiusgui.utils.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

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

        logger.info("---------------- Application started----------------");
    }


}
