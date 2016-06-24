package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Switch;

import java.util.List;

/**
 * Created by Dan on 10.06.2016.
 */
public interface ClientsConfigFileService {

    List<Switch> readFile();

    List<Switch> parseList(List<String> list);

    List<String> findSubList(List<String> list, Integer index);

    List<String> removeComments(List<String> list);

    String parseValue(String string, String pattern);
}
