package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Switch;
import lv.freeradiusgui.utils.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class ClientsConfigFileServiceImpl implements ClientsConfigFileService{

    public static final String CLIENT_PATTERN = "^client (.+)\\{";
    public static final String SECRET_PATTERN = "^secret = (.+)$";
    public static final String SHORTNAME_PATTERN = "^shortname = (.+)$";
    public static final String IPADDR_PATTERN = "^ipaddr = (.+)$";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AppConfig appConfig;

    @Override
    public List<Switch> readFile(){
        List<String> listFromConfig = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(appConfig.getPathToClientsConfFile()))) {

            //br returns as stream and convert it into a List
            listFromConfig = reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            logger.error("Error reading file  '" + appConfig.getPathToClientsConfFile() + "'");
            logger.error("STACK TRACE: ",e);
            return null;
        }
        logger.info("Successfully loaded '" + appConfig.getPathToClientsConfFile() + "'" +  " file.");
        return parseList(listFromConfig);
    }

    @Override
    public boolean saveFile(List<Switch> list) {
        List<String> stringList = new ArrayList<>();

        for (Switch aSwitch : list){
            stringList.add("client " + aSwitch.getIp() + " {");
            stringList.add(" secret = " + aSwitch.getSecret());
            stringList.add(" shortname = " + aSwitch.getName());
            stringList.add("}");
        }

        try {
            writeListToFile(stringList);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void writeListToFile(List<String> list) throws IOException {
        Path path = Paths.get(appConfig.getPathToClientsConfFile());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String str : list) {
                writer.write(str);
                writer.newLine();
            }
            writer.close();
        }
    }


    public List<Switch> parseList(List<String> list){
        List<Switch> switchList = new ArrayList<>();
        list = removeComments(list);
        Integer index = 0;
        do {
            List<String> subList = findSubList(list, index);
            index = index + subList.size();

            if (!subList.isEmpty()){
                switchList.add(parseSwitch(subList));
            }

        } while (index < list.size()-1);

        return switchList;
    }

    private Switch parseSwitch(List<String> list) {
        Switch newSwitch = new Switch();
            for (int index = 0; index < list.size(); index ++){
                String string = list.get(index);
                if (string.contains("client")){
                    newSwitch.setIp(parseValue(list.get(index), CLIENT_PATTERN));
                }
                if (string.contains("secret")){
                    newSwitch.setSecret(parseValue(list.get(index), SECRET_PATTERN));
                }
                if (string.contains("shortname")){
                    newSwitch.setName(parseValue(list.get(index), SHORTNAME_PATTERN));
                }
                if (string.contains("ipaddr")){
                    newSwitch.setName(newSwitch.getIp());
                    newSwitch.setIp(parseValue(list.get(index), IPADDR_PATTERN));
                }
            }
        return newSwitch;
    }

    public String parseValue(String string, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (m.find()) return m.group(1).trim(); else return "";
    }

    public List<String> findSubList(List<String> list, Integer index) {
        List<String> result = new ArrayList<>();

        while (!list.get(index).contains("client")){
            index++;
        }

        result.add(list.get(index));

        while (true){
            index ++;
            String currentString = list.get(index);
            if (currentString.contains("}")) {
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
