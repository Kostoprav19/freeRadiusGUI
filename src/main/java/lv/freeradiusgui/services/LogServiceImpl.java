package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.logDAO.LogDAO;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.services.filesServices.LogFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
@Service
public class LogServiceImpl implements LogService{

    @Autowired
    private LogDAO logDAO;

    @Autowired
    private LogFileService logFileService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(Log log) {
        return logDAO.store(log);
    }

    @Override
    public boolean storeAll(List<Log> logList) {
        Log lastRecord = logDAO.getLast();
        LocalDateTime lastRecordTime;
        if (lastRecord != null) { //If no logs in DB yet
            lastRecordTime = lastRecord.getTimeOfRegistration();
            logList = removeOldRecords(logList, lastRecordTime);
        }
        boolean result = logDAO.storeAll(logList);
        if (result) {
            logger.info("Successfully written log records to database.");
        } else {
            logger.error("Failed to write log records to database");
        }
        return result;
    }

    private List<Log> removeOldRecords(List<Log> logList, LocalDateTime lastRecord) {
        List<Log> result = new ArrayList<>();
        for (Log log : logList){
            if (log.getTimeOfRegistration().isAfter(lastRecord)) result.add(log);
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
    //    logger.info("sDate = " + sDate);
   //     logger.info("eDate = " + eDate);
        return logDAO.getByDate(sDate, eDate);
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
    public String loadFromFile(LocalDateTime date) {
        List<Log> listFromFile = logFileService.readListFromFile(date);
        if (listFromFile == null) {
            return null;
        } else {
            storeAll(listFromFile);
            return logFileService.getFileName(date);
        }
    }

    @Override
    public Integer countRejected(List<Log> list) {
        if ( (list == null) || (list.isEmpty()) ) return 0;
        int count = 0;
        for (Log log : list){
            if (log.getStatus() == Log.STATUS_REJECT) count++;
        }
        return count;
    }

}
