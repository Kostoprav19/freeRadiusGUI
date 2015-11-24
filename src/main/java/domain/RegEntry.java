package domain;

import java.time.LocalDateTime;

/**
 * Created by Dan on 24.11.2015.
 */
public class RegEntry {
    private int id;
    private String mac;
    private int onSwitch;
    private int onPort;
    private int speed;
    private LocalDateTime timeOfRegistration;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getOnSwitch() {
        return onSwitch;
    }

    public void setOnSwitch(int onSwitch) {
        this.onSwitch = onSwitch;
    }

    public int getOnPort() {
        return onPort;
    }

    public void setOnPort(int onPort) {
        this.onPort = onPort;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public LocalDateTime getTimeOfRegistration() {
        return timeOfRegistration;
    }

    public void setTimeOfRegistration(LocalDateTime timeOfRegistration) {
        this.timeOfRegistration = timeOfRegistration;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegEntry regEntry = (RegEntry) o;

        return !(getMac() != null ? !getMac().equals(regEntry.getMac()) : regEntry.getMac() != null);

    }

    @Override
    public int hashCode() {
        return getMac() != null ? getMac().hashCode() : 0;
    }
}
