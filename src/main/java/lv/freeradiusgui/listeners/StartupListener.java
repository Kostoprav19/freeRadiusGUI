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

        logger.info("Loading clients.conf file");
        switchService.reloadFromConfig();
        logger.info("Loading users file");
        deviceService.reloadFromConfig();

        LocalDateTime today = LocalDateTime.now();
        logger.info("Loading log file");
        logService.loadFromFile(today);

         server.updateStatuses();

        server.setTodayRejectedCount(logService.countRejected(logService.getByDate(today)));
        logger.info("Today rejected count: " + server.getTodayRejectedCount());

        logger.info("---------------- Application started----------------");
    }


}
