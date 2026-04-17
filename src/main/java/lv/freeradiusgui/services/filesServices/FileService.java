package lv.freeradiusgui.services.filesServices;

import java.util.List;

public interface FileService<T> {
  List<T> readListFromFile();

  boolean saveListToFile(List<T> list);
}
