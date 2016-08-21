package lv.freeradiusgui.services.filesServices;

import java.util.List;

/**
 * Created by Daniels on 20.08.2016..
 */
public interface FileService<T> {

    List<T> readListFromFile();

    boolean saveListToFile(List<T> list);
}
