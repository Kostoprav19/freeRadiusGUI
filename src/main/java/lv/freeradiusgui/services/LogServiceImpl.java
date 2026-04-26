package lv.freeradiusgui.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.repositories.DeviceRepository;
import lv.freeradiusgui.repositories.LogRepository;
import lv.freeradiusgui.repositories.SwitchRepository;
import lv.freeradiusgui.services.filesServices.LogFileService;
import lv.freeradiusgui.services.serverServices.ServerService;
import lv.freeradiusgui.utils.OperationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

    @Autowired private LogRepository logRepository;

    @Autowired private SwitchRepository switchRepository;

    @Autowired private DeviceRepository deviceRepository;

    @Autowired private LogFileService logFileService;

    @Autowired ServerService serverService;

    DateTimeFormatter displayFormatter =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").withLocale(Locale.ENGLISH);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean store(Log log) {
        if (log == null) return false;
        try {
            logRepository.save(log);
            return true;
        } catch (Exception e) {
            logger.error("Failed to store log", e);
            return false;
        }
    }

    @Override
    public boolean storeAll(List<Log> listFromFile) {
        if (listFromFile == null) return false;
        try {
            logRepository.saveAll(listFromFile);
            logger.info("Successfully written log records to database.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to write log records to database.", e);
            return false;
        }
    }

    @Override
    public Log getById(Integer id) {
        if (id == null || id < 0) return null;
        Log log = logRepository.findById(id).orElse(null);
        if (log != null) hydrate(java.util.Collections.singletonList(log));
        return log;
    }

    @Override
    public List<Log> getByDate(LocalDateTime date) {
        LocalDateTime sDate = date.with(LocalTime.MIDNIGHT);
        LocalDateTime eDate = sDate.plusDays(1);
        List<Log> list =
                logRepository
                        .findByTimeOfRegistrationGreaterThanEqualAndTimeOfRegistrationLessThanOrderByIdDesc(
                                sDate, eDate);
        hydrate(list);
        return list;
    }

    @Override
    public List<Log> getToday() {
        LocalDateTime today = LocalDateTime.now();
        return getByDate(today);
    }

    @Override
    public boolean deleteByDate(LocalDateTime date) {
        List<Log> list = getByDate(date);
        if (list == null) return false;
        try {
            logRepository.deleteAll(list);
            logger.info(
                    "Successfully deleted log records from database by date: "
                            + date.format(displayFormatter));
            return true;
        } catch (Exception e) {
            logger.error(
                    "Failed to delete log records from database by date: "
                            + date.format(displayFormatter),
                    e);
            return false;
        }
    }

    @Override
    public Log getLastByMac(String mac, List<Log> list) {
        if (mac.isEmpty()) return null;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getMac().equals(mac)) return list.get(i);
        }
        return null;
    }

    @Override
    public List<Log> getAll() {
        List<Log> list = new ArrayList<>();
        logRepository.findAll().forEach(list::add);
        hydrate(list);
        return list;
    }

    @Override
    public Long getCount() {
        return logRepository.count();
    }

    @Override
    public OperationResult loadFromFile(LocalDateTime date) {
        List<Log> listFromFile = logFileService.readListFromFile(date);

        OperationResult result =
                new OperationResult(true, logFileService.getFileName(date), listFromFile);
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

        for (Log log : list) {
            if (log.getStatus() == Log.STATUS_REJECT) result.add(log);
        }
        return result;
    }

    @Override
    public Integer countRejected(List<Log> list) {
        return filterRejectedLogs(list).size();
    }

    /** Populates each log's transient {@link Log#getSwitch()} and {@link Log#getDevice()}. */
    private void hydrate(List<Log> logs) {
        if (logs == null || logs.isEmpty()) return;

        Set<Integer> switchIds = new HashSet<>();
        Set<String> macs = new HashSet<>();
        for (Log log : logs) {
            if (log.getSwitchId() != null) switchIds.add(log.getSwitchId());
            if (log.getMac() != null) macs.add(log.getMac());
        }

        Map<Integer, Switch> switchById = new HashMap<>();
        if (!switchIds.isEmpty()) {
            switchRepository.findAllById(switchIds).forEach(s -> switchById.put(s.getId(), s));
        }

        Map<String, Device> deviceByMac = new HashMap<>();
        if (!macs.isEmpty()) {
            deviceRepository.findByMacIn(macs).forEach(d -> deviceByMac.put(d.getMac(), d));
        }

        for (Log log : logs) {
            Integer sid = log.getSwitchId();
            if (sid != null) {
                Switch s = switchById.get(sid);
                if (s != null) log.setSwitch(s);
            }
            if (log.getMac() != null) {
                Device d = deviceByMac.get(log.getMac());
                if (d != null) log.setDevice(d);
            }
        }
    }
}
