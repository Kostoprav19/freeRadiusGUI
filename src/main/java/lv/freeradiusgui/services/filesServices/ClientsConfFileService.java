package lv.freeradiusgui.services.filesServices;

import lv.freeradiusgui.domain.Switch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dan on 08.06.2016.
 */
@Service
public class ClientsConfFileService extends AbstractFileServices implements FileService<Switch> {

    public static final String CLIENT_PATTERN = "^client (.+)\\{";
    public static final String SECRET_PATTERN = "^secret = (.+)$";
    public static final String SHORTNAME_PATTERN = "^shortname = (.+)$";
    public static final String IPADDR_PATTERN = "^ipaddr = (.+)$";

    @Override
    public List<Switch> readListFromFile(){
        List<String> listFromFile = readFile(appConfig.getPathToClientsConfFile());
        listFromFile = removeComments(listFromFile);
        return parseList(listFromFile);
    }

    @Override
    public boolean saveListToFile(List<Switch> list) {
        List<String> stringList = new ArrayList<>();

        for (Switch aSwitch : list){
            stringList.add("client " + aSwitch.getIp() + " {");
            stringList.add(" secret = " + aSwitch.getSecret());
            stringList.add(" shortname = " + aSwitch.getName());
            stringList.add("}");
        }

        return writeFile(stringList, appConfig.getPathToClientsConfFile());
    }


    private List<Switch> parseList(List<String> list){
        List<Switch> switchList = new ArrayList<>();
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

   private List<String> findSubList(List<String> list, Integer index) {
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
}
