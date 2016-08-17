package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.logDAO.LogDAO;
import lv.freeradiusgui.domain.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public boolean store(Log log) {
        return logDAO.store(log);
    }

    @Override
    public boolean storeAll(List<Log> logList) {
        LocalDateTime lastRecordTime = logDAO.getLast().getTimeOfRegistration();
        logList = removeOldRecords(logList, lastRecordTime);
        return logDAO.storeAll(logList);
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
    public String loadFromFile() {
        List<Log> listFromFile = logFileService.readFile();
        if (listFromFile == null) {
            return null;
        } else {
            storeAll(listFromFile);
            return logFileService.getFileName();
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
