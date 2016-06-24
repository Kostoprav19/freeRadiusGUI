package lv.freeradiusgui.services;

import lv.freeradiusgui.dao.logDAO.LogDAO;
import lv.freeradiusgui.domain.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return logDAO.storeAll(logList);
    }

    @Override
    public Log getById(Integer id) {
        return logDAO.getById(id);
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
            logDAO.storeAll(listFromFile);
            return logFileService.getFileName();
        }
    }

}
