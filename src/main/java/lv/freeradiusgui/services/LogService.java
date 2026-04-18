package lv.freeradiusgui.services;

import java.time.LocalDateTime;
import java.util.List;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.utils.OperationResult;

public interface LogService {
    boolean store(Log log);

    boolean storeAll(List<Log> logList);

    Log getById(Integer id);

    List<Log> getByDate(LocalDateTime date);

    List<Log> getToday();

    boolean deleteByDate(LocalDateTime date);

    Log getLastByMac(String mac, List<Log> list);

    List<Log> getAll();

    List<Log> getAllByCriteria(String fieldName, Object object);

    Long getCount();

    OperationResult loadFromFile(LocalDateTime date);

    OperationResult loadFromFileToday();

    Integer countRejected(List<Log> list);
}
