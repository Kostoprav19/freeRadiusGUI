package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Log;
import lv.freeradiusgui.utils.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Dan on 08.06.2016.
 */
@Service
public class LogFileServiceImpl implements LogFileService{

    public static final String MAC_PATTERN = "MAC:(([0-9a-fA-F]){12})";
    public static final String ACCESS_PATTERN = "Packet-Type = Access-(\\w+)";
    public static final String PORT_PATTERN = "Port: (\\d+),";
    public static final String PORTSPEED_PATTERN = "Connection: CONNECT Ethernet (.+?)M";
    public static final String SWITCHIP_PATTERN = "Switch IP: (.+?),";

    @Autowired
    AppConfig appConfig;

    @Autowired
    SwitchService switchService;

    @Autowired
    DeviceService deviceService;

    public String fileName;

    @Override
    public List<Log> readFile(){
        List<String> listFromConfig = new ArrayList<>();

        fileName = getFileName();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

            //br returns as stream and convert it into a List
            listFromConfig = br.lines().collect(Collectors.toList());

        } catch (IOException e) {
            System.out.println("Error reading file  '" + fileName + "'");
            e.printStackTrace();
            return null;
        }

        return parseList(listFromConfig);
    }

    @Override
    public String getFileName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        //return "auth-detail-" + formatter.format(LocalDateTime.now());
        return appConfig.getPathToLogDirectory() + "/auth-detail-20160620";
    }


    public List<Log> parseList(List<String> list){
        List<Log> logList = new ArrayList<>();
        list = removeComments(list);
        Integer index = 0;
        do {
            List<String> subList = findSubList(list, index);
            index = index + subList.size();

            if (!subList.isEmpty()){
                logList.add(parseLog(subList));
            }

        } while (index < list.size()-1);

        return logList;
    }

    private Log parseLog(List<String> list) {
        Log newLog = new Log();
            for (int index = 0; index < list.size(); index ++){
                String string = list.get(index);
                if (string.contains(Integer.toString(LocalDateTime.now().getYear()))){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
                    LocalDateTime dateTime;
                    try {
                        dateTime = LocalDateTime.parse(string, formatter);
                    } catch (DateTimeException e){
                        System.out.println("Can't be formatted.");
                        e.printStackTrace();
                        dateTime = null;
                    }
                    newLog.setTimeOfRegistration(dateTime);
                }
                if (string.contains("Reply-Message")){
                    newLog.setMac(parseValue(list.get(index), MAC_PATTERN));

                    newLog.setDevice(deviceService.getByMac(newLog.getMac()));

                    newLog.setSwitchPort(Integer.parseInt(parseValue(list.get(index), PORT_PATTERN)));

                    newLog.setPortSpeed(Integer.parseInt(parseValue(list.get(index), PORTSPEED_PATTERN)));

                    if (string.contains("Full duplex")) newLog.setDuplex(1); else newLog.setDuplex(0);

                    String switchIP = parseValue(list.get(index), SWITCHIP_PATTERN);
                    newLog.setSwitch(switchService.getByIp(switchIP));
                }
                if (string.contains("Packet-Type")){
                    if (parseValue(list.get(index), ACCESS_PATTERN).equals("Accept")) {
                        newLog.setStatus(1);
                    } else {
                        newLog.setStatus(0);
                    }
                }
            }
        return newLog;
    }

    public String parseValue(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) return m.group(1).trim(); else return "";
    }

    public List<String> findSubList(List<String> list, Integer index) {
        List<String> result = new ArrayList<>();

        while (!list.get(index).contains("Packet-Type")){
            index++;
        }
        index--; //Because sublist starts in previous string;

        result.add(list.get(index));

        while (true){
            index ++;
            String currentString = list.get(index);
            if (currentString.contains("Reply-Message")) {
                result.add(currentString);
                break;
            }
            result.add(currentString);
        }

        return result;
    }


    public List<String> removeComments(List<String> list) {
        List<String> result = new ArrayList<>();

        for (String string : list) {
            string = string.trim();
            int position = string.indexOf("#");
            if (string.equals("")) position = 0; //do not add empty lines
            switch (position){
                case -1: result.add(string); break;
                case 0: break;
                default: result.add(string.substring(0, position).trim()); break;
            }
        }

        return result;
    }
}
