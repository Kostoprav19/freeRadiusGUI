package lv.freeradiusgui.scheduler;

import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.DeviceService;
import lv.freeradiusgui.services.LogService;
import lv.freeradiusgui.services.mailServices.MailService;
import lv.freeradiusgui.services.serverServices.ServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Created by Daniels on 16.09.2016..
 */
@Component
public class ScheduledTasks {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LogService logService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    ServerService serverService;

    @Autowired
    MailService mailService;


    @Scheduled(initialDelay=300000, fixedRate = 300000) //every 5 minutes
    public void refreshApplication() {

        logger.info("---------------- Scheduled task started ----------------");

        serverService.updateStatuses();

        logService.loadFromFileToday();

        logger.info("Updating device statistics");
        deviceService.updateStatistics();

        logger.info("---------------- Scheduled task ended ----------------");
    }

}
