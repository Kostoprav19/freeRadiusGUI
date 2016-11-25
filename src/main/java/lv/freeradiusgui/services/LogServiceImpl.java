package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.logDAO.LogDAO;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Server;
import lv.freeradiusgui.services.mailServices.MailService;
import lv.freeradiusgui.services.serverServices.ServerService;
import lv.freeradiusgui.utils.OperationResult;
import lv.freeradiusgui.services.filesServices.LogFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Dan on 29.04.2016.
 */
@Service
public class LogServiceImpl implements LogService{

    @Autowired
    private LogDAO logDAO;

    @Autowired
    private LogFileService logFileService;

    @Autowired
    ServerService serverService;

    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withLocale(Locale.ENGLISH);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(Log log) {
        return logDAO.store(log);
    }

    @Override
    public boolean storeAll(List<Log> listFromFile) {
        boolean result = logDAO.storeAll(listFromFile);
        if (result) {
            logger.info("Successfully written log records to database.");
        } else {
            logger.error("Failed to write log records to database.");
        }
        return result;
    }

    @Override
    public Log getById(Integer id) {
        return logDAO.getById(id);
    }

    @Override
    public List<Log> getByDate(LocalDateTime date) {

        LocalDateTime sDate = date.with(LocalTime.MIDNIGHT);
        LocalDateTime eDate = sDate.plusDays(1);
        return logDAO.getByDate(sDate, eDate);
    }

    @Override
    public List<Log> getToday() {
        LocalDateTime today = LocalDateTime.now();
        return getByDate(today);
    }

    @Override
    public boolean deleteByDate(LocalDateTime date) {
        List<Log> list = getByDate(date);
        boolean result = logDAO.deleteAll(list);
        if (result) {
            logger.info("Successfully deleted log records from database by date: " + date.format(displayFormatter));
        } else {
            logger.error("Failed to delete log records from database by date: " + date.format(displayFormatter));
        }
        return result;
    }

    @Override
    public Log getLastByMac(String mac, List<Log> list) {
        if (mac.isEmpty()) return null;
        for (int i = list.size()-1; i>=0; i--){
            if (list.get(i).getMac().equals(mac)) return list.get(i);
        }
        return null;
    }

    @Override
    public List<Log> getAll() {
        return logDAO.getAll();
    }

    @Override
    public List<Log> getAllByCriteria(String fieldName, Object object) {
        return logDAO.getAllByCriteria(fieldName, object);
    }

    @Override
    public Long getCount() {
        return logDAO.getCount();
    }


    @Override
    public OperationResult loadFromFile(LocalDateTime date) {
        List<Log> listFromFile = logFileService.readListFromFile(date);

        OperationResult result = new OperationResult(true, logFileService.getFileName(date), listFromFile);
        if (listFromFile == null) {
            result.ok = false;
            return result;
        }
        deleteByDate(date);
        storeAll(listFromFile);

        if (date.toLocalDate().equals(LocalDate.now())) {
            List<Log> rejectedLogsList = filterRejectedLogs(listFromFile);
            serverService.setTodayRejected(rejectedLogsList);
        }

        return result;
    }

    @Override
    public OperationResult loadFromFileToday() {
        LocalDateTime today = LocalDateTime.now();
        return loadFromFile(today);
    }

    private List<Log> filterRejectedLogs(List<Log> list) {
        List<Log> result = new ArrayList<>();

        for (Log log : list){
            if (log.getStatus() == Log.STATUS_REJECT) result.add(log);
        }
        return result;
    }

    @Override
    public Integer countRejected(List<Log> list) {
        return filterRejectedLogs(list).size();
    }

}
