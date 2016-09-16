package lv.freeradiusgui.scheduler;

import lv.freeradiusgui.config.AppConfig;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.LogService;
import lv.freeradiusgui.services.filesServices.FileOperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Created by Daniels on 16.09.2016..
 */
@Component
public class ScheduledTasks {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LogService logService;

    @Autowired
    Server server;

    @Scheduled(initialDelay=300000, fixedRate = 300000) //every 5 minutes
    public void refreshApplication() {

        logger.info("---------------- Scheduled task started ----------------");

        server.updateStatuses();

        LocalDateTime today = LocalDateTime.now();
        logService.loadFromFile(today);


        if (server.setTodayRejectedCount(logService.countRejected(logService.getByDate(today)))) {
            logger.info("Rejected device detected!");
        }

        logger.info("---------------- Scheduled task ended ----------------");
    }
}
