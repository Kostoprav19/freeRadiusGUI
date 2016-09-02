package lv.freeradiusgui.services.filesServices;

/**
 * Created by Daniels on 01.09.2016..
 */
public class FileOperationResult<T> {
    public boolean ok;
    public String message;
    public T object;

    public FileOperationResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public FileOperationResult(boolean ok, String message, T object) {
        this.ok = ok;
        this.message = message;
        this.object = object;
    }
}
