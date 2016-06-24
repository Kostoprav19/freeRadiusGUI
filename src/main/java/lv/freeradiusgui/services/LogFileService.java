package lv.freeradiusgui.services;

import lv.freeradiusgui.domain.Log;

import java.util.List;

/**
 * Created by Dan on 15.06.2016.
 */
public interface LogFileService {

    List<Log> readFile();

    String getFileName();

    List<Log> parseList(List<String> list);

    List<String> findSubList(List<String> list, Integer index);

    List<String> removeComments(List<String> list);

    String parseValue(String string, String pattern);
}
