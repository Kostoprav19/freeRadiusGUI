package lv.freeradiusgui.domain;

import java.time.LocalDateTime;

/**
 * Created by Dan on 24.11.2015.
 */
public class Log {
    private Integer id;
    private String mac;
    private Switch aSwitch;
    private Integer switchPort;
    private Integer portSpeed;
    private LocalDateTime timeOfRegistration;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        mac = mac.replaceAll("[^a-fA-F0-9]", ""); //normalize
        this.mac = mac;
    }

    public Switch getSwitch() {
        return aSwitch;
    }

    public void setSwitch(Switch aSwitch) {
        this.aSwitch = aSwitch;
    }

    public Integer getSwitchPort() {
        return switchPort;
    }

    public void setSwitchPort(Integer switchPort) {
        this.switchPort = switchPort;
    }

    public Integer getPortSpeed() {
        return portSpeed;
    }

    public void setPortSpeed(Integer portSpeed) {
        this.portSpeed = portSpeed;
    }

    public LocalDateTime getTimeOfRegistration() {
        return timeOfRegistration;
    }

    public void setTimeOfRegistration(LocalDateTime timeOfRegistration) {
        this.timeOfRegistration = timeOfRegistration;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Log)) return false;

        Log log = (Log) o;

        return !(getMac() != null ? !getMac().equals(log.getMac()) : log.getMac() != null);

    }

    @Override
    public int hashCode() {
        return getMac() != null ? getMac().hashCode() : 0;
    }
}
