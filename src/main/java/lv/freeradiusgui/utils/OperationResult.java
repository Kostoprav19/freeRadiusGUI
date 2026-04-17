package lv.freeradiusgui.utils;

public class OperationResult<T> {

  public static final boolean SUCCESS = true;
  public static final boolean FAIL = false;

  public boolean ok;
  public String message;
  public T object;

  public OperationResult(boolean ok, String message) {
    this.ok = ok;
    this.message = message;
  }

  public OperationResult(boolean ok, String message, T object) {
    this.ok = ok;
    this.message = message;
    this.object = object;
  }
}
