package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Log;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Dan on 29.04.2016.
 */
public interface LogService {

    boolean store(Log log);

    boolean storeAll(List<Log> logList);

    Log getById(Integer id);

    Log getByDate(LocalDateTime date);

    Log getLastByMac(String mac, List<Log> list);

    List<Log> getAll();

    List<Log> getAllByCriteria(String fieldName, Object object);

    Long getCount();

    String loadFromFile();

    Integer countRejected(List<Log> list);
}
