package domain;

import java.time.LocalDateTime;

/**
 * Created by daniels on 24.11.2015.
 */
public class Device {
    private int id;
    private String mac;
    private String name;
    private String description;
    private Switch aSwitch;
    private int switchPort;
    private int portSpeed;
    private LocalDateTime timeOfRegistration;
    private LocalDateTime lastSeen;
    private int access;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Device device = (Device) o;

        return !(getMac() != null ? !getMac().equals(device.getMac()) : device.getMac() != null);

    }

    @Override
    public int hashCode() {
        return getMac() != null ? getMac().hashCode() : 0;
    }
}
