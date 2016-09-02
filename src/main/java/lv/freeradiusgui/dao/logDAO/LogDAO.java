package lv.freeradiusgui.dao.logDAO;

import lv.freeradiusgui.domain.Log;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Daniels on 25.11.2015..
 */

public interface LogDAO {

    boolean store(Log log);

    boolean storeAll(List<Log> logList);

    Log getById(Integer id);

    Log getLastByMac(String mac);

    Log getLast();

    List<Log> getAll();

    List<Log> getByDate(LocalDateTime sDate, LocalDateTime eDate);

    boolean deleteAll(List<Log> list);

    List<Log> getAllByCriteria(String fieldName, Object object);

    Long getCount();

}
