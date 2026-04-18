package lv.freeradiusgui.services.filesServices;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.services.DeviceService;
import lv.freeradiusgui.services.SwitchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogFileService extends AbstractFileServices implements FileService<Log> {

    public static final String MAC_PATTERN = "MAC:(([0-9a-fA-F]){12})";
    public static final String ACCESS_PATTERN = "Packet-Type = Access-(\\w+)";
    public static final String PORT_PATTERN = "Port: (\\d+),";
    public static final String PORTSPEED_PATTERN = "Connection: CONNECT Ethernet (.+?)M";
    public static final String SWITCHIP_PATTERN = "Switch IP: (.+?),";

    @Autowired SwitchService switchService;

    @Autowired DeviceService deviceService;

    private List<Device> deviceList;
    private List<Switch> switchList;

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
    DateTimeFormatter formatter2 =
            DateTimeFormatter.ofPattern("EEE MMM  d HH:mm:ss yyyy").withLocale(Locale.ENGLISH);

    LocalDateTime currentLogFileDateTime;

    public List<Log> readListFromFile(LocalDateTime date) {
        this.currentLogFileDateTime = date;
        List<String> listFromFile = readFile(getFileName(date));
        if (listFromFile == null) return null;

        listFromFile = removeComments(listFromFile);
        return parseList(listFromFile);
    }

    @Override
    public List<Log> readListFromFile() {
        List<String> listFromFile = readFile(getFileName(LocalDateTime.now()));
        if (listFromFile == null) return null;

        listFromFile = removeComments(listFromFile);
        return parseList(listFromFile);
    }

    @Override
    public boolean saveListToFile(List<Log> list) {
        return false;
    }

    public String getFileName(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String fileName = "auth-detail-" + date.format(formatter);
        return appConfig.getPathToLogDirectory() + "/" + fileName;
    }

    private List<Log> parseList(List<String> list) {
        deviceList = deviceService.getAll();
        switchList = switchService.getAll();
        List<Log> logList = new ArrayList<>();
        Integer index = 0;
        while (index < list.size() - 1) {
            List<String> subList = findSubList(list, index);
            // findSubList returns empty when there are no more Packet-Type
            // records in the remainder of the file (e.g. only trailing
            // Timestamp / blank lines left after the last record).
            // Without this break parseList spins forever because index
            // never advances past the tail — FreeRADIUS 3.2 always emits a
            // synthetic Timestamp line after Reply-Message, so the tail is
            // always hit on a non-empty file.
            if (subList.isEmpty()) break;
            index = index + subList.size();
            logList.add(parseLog(subList));
        }

        return logList;
    }

    private Log parseLog(List<String> list) {
        Log newLog = new Log();

        for (int index = 0; index < list.size(); index++) {
            String string = list.get(index);

            if (string.contains(Integer.toString(LocalDateTime.now().getYear()))) {
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(string, formatter);
                } catch (DateTimeException e) {
                    dateTime = LocalDateTime.parse(string, formatter2);
                }
                newLog.setTimeOfRegistration(dateTime);
            }

            if (string.contains("Reply-Message")) {
                newLog.setMac(parseValue(list.get(index), MAC_PATTERN));

                newLog.setDevice(deviceService.getByMac(newLog.getMac(), deviceList));

                newLog.setSwitchPort(Integer.parseInt(parseValue(list.get(index), PORT_PATTERN)));

                newLog.setPortSpeed(
                        Integer.parseInt(parseValue(list.get(index), PORTSPEED_PATTERN)));

                if (string.contains("Full duplex")) newLog.setDuplex(1);
                else newLog.setDuplex(0);

                String switchIP = parseValue(list.get(index), SWITCHIP_PATTERN);
                newLog.setSwitch(switchService.getByIp(switchIP, switchList));
            }
            if (string.contains("Packet-Type")) {
                if (parseValue(list.get(index), ACCESS_PATTERN).equals("Accept")) {
                    newLog.setStatus(1);
                } else {
                    newLog.setStatus(0);
                }
            }
        }
        return newLog;
    }

    private List<String> findSubList(List<String> list, Integer index) {
        List<String> result = new ArrayList<>();

        // Scan forward to the next Packet-Type line. FreeRADIUS 3.2 appends
        // a synthetic Timestamp attribute after Reply-Message that the
        // original 2.x-era parser wasn't expecting, so we have to tolerate
        // trailing lines after the last record and bail instead of reading
        // off the end of the list.
        while (index < list.size() && !list.get(index).contains("Packet-Type")) {
            index++;
        }
        if (index >= list.size()) {
            return result;
        }
        index--; // Because sublist starts in previous string;

        result.add(list.get(index));

        while (true) {
            index++;
            if (index >= list.size()) break;
            String currentString = list.get(index);
            if (currentString.contains("Reply-Message")) {
                result.add(currentString);
                break;
            }
            result.add(currentString);
        }

        return result;
    }
}
