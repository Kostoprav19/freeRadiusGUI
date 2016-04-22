package lv.freeradiusgui.domain;

import java.time.LocalDateTime;

/**
 * Created by Dan on 24.11.2015.
 */
public class RegEntry {
    private int id;
    private String mac;
    private Switch aSwitch;
    private int switchPort;
    private int portSpeed;
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

    public Switch getSwitch() {
        return aSwitch;
    }

    public void setSwitch(Switch aSwitch) {
        this.aSwitch = aSwitch;
    }

    public int getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(int switchPort) {
        this.switchPort = switchPort;
    }

    public int getPortSpeed() {
        return portSpeed;
    }

    public void setPortSpeed(int portSpeed) {
        this.portSpeed = portSpeed;
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
