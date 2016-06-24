package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Device;

import java.util.List;

/**
 * Created by Dan on 15.06.2016.
 */
public interface UsersFileService {

    List<Device> readFile();

    List<Device> parseList(List<String> list);

    List<String> findSubList(List<String> list, Integer index);

    List<String> removeComments(List<String> list);

    String parseValue(String string, String pattern);
}
