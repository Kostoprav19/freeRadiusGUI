package domain;

import java.util.Date;

/**
 * Created by Daniels on 20.10.2015..
 */

public class LogEntry {
    private long logId;
    private long userId;
    private long accessObjectID;
    private String ip;
    private String command;
    private String value;
    private Date date;

    public long getId() {
        return logId;
    }

    public void setId(long logId) {
        this.logId = logId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAccessObjectID() {
        return accessObjectID;
    }

    public void setAccessObjectID(long accessObjectID) {
        this.accessObjectID = accessObjectID;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogEntry logEntry = (LogEntry) o;

        if (logId != logEntry.logId) return false;
        //if (getUserId() != logEntry.getUserId()) return false;
        if (getAccessObjectID() != logEntry.getAccessObjectID()) return false;
        if (getIp() != null ? !getIp().equals(logEntry.getIp()) : logEntry.getIp() != null) return false;
        if (getCommand() != null ? !getCommand().equals(logEntry.getCommand()) : logEntry.getCommand() != null)
            return false;
        if (getValue() != null ? !getValue().equals(logEntry.getValue()) : logEntry.getValue() != null) return false;
        return !(getDate() != null ? !getDate().equals(logEntry.getDate()) : logEntry.getDate() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (logId ^ (logId >>> 32));
        result = 31 * result + (int) (getUserId() ^ (getUserId() >>> 32));
        result = 31 * result + (int) (getAccessObjectID() ^ (getAccessObjectID() >>> 32));
        result = 31 * result + (getIp() != null ? getIp().hashCode() : 0);
        result = 31 * result + (getCommand() != null ? getCommand().hashCode() : 0);
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }
}
