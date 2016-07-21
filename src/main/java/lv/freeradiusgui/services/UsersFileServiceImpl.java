package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.utils.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Dan on 08.06.2016.
 */
@Service
public class UsersFileServiceImpl implements UsersFileService{
    public static final String MAC_PATTERN = "^(([0-9a-fA-F]){12})";
    public static final String ACCESS_PATTERN = "Auth-Type := (\\w+)";
    public static final String DEVICENAME_PATTERN = "Reply-Message = \"(.+?),";

    @Autowired
    AppConfig appConfig;

    public List<Device> readFile(){
        List<String> listFromConfig = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(appConfig.getPathToUsersFile()))) {

            //br returns as stream and convert it into a List
            listFromConfig = br.lines().collect(Collectors.toList());

        } catch (IOException e) {
            System.out.println("Error reading file  '" + appConfig.getPathToUsersFile() + "'");
            e.printStackTrace();
            return null;
        }

        return parseList(listFromConfig);
    }


    public List<Device> parseList(List<String> list){
        List<Device> deviceList = new ArrayList<>();
        list = removeComments(list);
        Integer index = 0;
        do {
            List<String> subList = findSubList(list, index);
            index = index + subList.size();

            if (!subList.isEmpty()){
                deviceList.add(parseDevice(subList));
            }

        } while (index < list.size()-1);

        return deviceList;
    }

    private Device parseDevice(List<String> list) {
        Device newDevice = new Device();
            for (int index = 0; index < list.size(); index ++){
                String string = list.get(index);
                if (string.contains("Reply-Message")){
                    newDevice.setName(parseValue(list.get(index), DEVICENAME_PATTERN));
                    newDevice.setType(detectType(newDevice.getName()));
                }
                if (string.contains("Auth-Type")){
                    newDevice.setMac(parseValue(list.get(index), MAC_PATTERN));
                    if (parseValue(list.get(index), ACCESS_PATTERN).equals("Accept")) {
                        newDevice.setAccess(1);
                    } else {
                        newDevice.setAccess(0);
                    }
                }
            }
        return newDevice;
    }

    private String detectType(String name) {
        if ((name.toUpperCase().contains("LJ")) ||
                (name.toUpperCase().contains("LASERJET")) ||
                (name.toUpperCase().contains("TRIUMF")) ||
                (name.toUpperCase().contains("DESIGNJET")) ||
                (name.toUpperCase().contains("XEROX")) ||
                (name.toUpperCase().contains("PHOTOSMART")) ||
                (name.toUpperCase().contains(" MFP ")) ||
                (name.toUpperCase().contains(" DJ ")) ||
                (name.toUpperCase().contains("KONICA")) ||
                (name.toUpperCase().contains("PRINTER")))
            return Device.TYPE_PRINTER;

        if ((name.toUpperCase().contains("TIMECAPSULE")) ||
                (name.toUpperCase().contains("ZABBIX")))
            return Device.TYPE_OTHER;

        return Device.TYPE_COMPUTER;
    }

    public String parseValue(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) return m.group(1).trim(); else return "";
    }

    public List<String> findSubList(List<String> list, Integer index) {
        List<String> result = new ArrayList<>();

        while (!list.get(index).contains("Auth-Type")){
            index++;
        }

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
