package lv.freeradiusgui.services.filesServices;

import lv.freeradiusgui.domain.Device;
import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.utils.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UsersFileService extends AbstractFileServices implements FileService<Device>{
    public static final String MAC_PATTERN = "^(([0-9a-fA-F]){12})";
    public static final String ACCESS_PATTERN = "Auth-Type := (\\w+)";
    public static final String DEVICENAME_PATTERN = "Reply-Message = \"(.+?),";

    @Override
    public List<Device> readListFromFile(){
        List<String> listFromFile = readFile(appConfig.getPathToUsersFile());
        listFromFile = removeComments(listFromFile);
        return parseList(listFromFile);
    }

    @Override
    public boolean saveListToFile(List<Device> list) {
        List<String> stringList = new ArrayList<>();

        for (Device device : list){
            String accessStr = (device.getAccess() == Device.ACCESS_ACCEPT) ? "Accept" : "Reject";
            stringList.add(device.getMac() + " Auth-Type := " + accessStr);
            stringList.add("        Reply-Message = \"" + device.getName() + ", Access-" + accessStr +
            ", MAC:%{User-Name}, Connection: %{Connect-Info}, Port: %{NAS-Port}, Switch IP: %{NAS-IP-Address}, Switch Name: %{NAS-Identifier}\"");
        }
        stringList.add("DEFAULT Auth-Type := Reject");
        stringList.add("        Reply-Message = \"Access-Reject, MAC:%{User-Name}, Connection: %{Connect-Info}, Port: %{NAS-Port}, Switch IP: %{NAS-IP-Address}, Switch Name: %{NAS-Identifier}\"");
//        Example:
//        041e64fc08c3  Auth-Type := Accept
//        Reply-Message = "ansis-imac-ethernet2, Access-Accept, MAC:%{User-Name}, Connection: %{Connect-Info}, Port: %{NAS-Port}, Switch IP: %{NAS-IP-Address}, Switch Name: %{NAS-Identifier}"


        return writeFile(stringList, appConfig.getPathToUsersFile());
    }

    private List<Device> parseList(List<String> list){
        List<Device> deviceList = new ArrayList<>();
        Integer index = 0;
        do {
            List<String> subList = findSubList(list, index);
            index = index + subList.size();

            if (!subList.isEmpty()){
                deviceList.add(parseDevice(subList));
            }

        } while (index < list.size()-1);

        deviceList.remove(deviceList.size()-1); //last record is DEFAULT
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
                        newDevice.setAccess(Device.ACCESS_ACCEPT);
                    } else {
                        newDevice.setAccess(Device.ACCESS_REJECT);
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

    private List<String> findSubList(List<String> list, Integer index) {
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
}
