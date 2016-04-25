package lv.freeradiusgui.domain;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by daniels on 24.11.2015.
 */
@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mac", unique = true)
    private String mac;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String description;

    @Transient
    @Column(name = "switch")
    private Switch aSwitch;

    @Column(name = "port")
    private int switchPort;

    @Column(name = "speed")
    private int portSpeed;

    @Column(name = "tor")
    @Type(type = "lv.freeradiusgui.utils.CustomLocalDateTime")
    private LocalDateTime timeOfRegistration;

    @Column(name = "lastseen")
    @Type(type = "lv.freeradiusgui.utils.CustomLocalDateTime")
    private LocalDateTime lastSeen;

    @Column(name = "access")
    private int access;

    public Device(){

    }

    public Device(String mac, String name, String description, Switch aSwitch, int switchPort, int portSpeed, LocalDateTime timeOfRegistration, LocalDateTime lastSeen, int access) {
        this.mac = mac;
        this.name = name;
        this.description = description;
        this.aSwitch = aSwitch;
        this.switchPort = switchPort;
        this.portSpeed = portSpeed;
        this.timeOfRegistration = timeOfRegistration;
        this.lastSeen = lastSeen;
        this.access = access;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

        if (!id.equals(device.id)) return false;
        return mac.equals(device.mac);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + mac.hashCode();
        return result;
    }

    public static class DeviceBuilder {

        private String mac;
        private String name;
        private String description;
        private Switch aSwitch;
        private int switchPort;
        private int portSpeed;
        private LocalDateTime timeOfRegistration;
        private LocalDateTime lastSeen;
        private int access;

        public DeviceBuilder() {
        }

        public DeviceBuilder withMac(String mac) {
            this.mac = mac;
            return this;
        }

        public DeviceBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DeviceBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DeviceBuilder onSwitch(Switch aSwitch) {
            this.aSwitch = aSwitch;
            return this;
        }

        public DeviceBuilder onPort(int switchPort) {
            this.switchPort = switchPort;
            return this;
        }

        public DeviceBuilder withPortSpeed(int portSpeed) {
            this.portSpeed = portSpeed;
            return this;
        }

        public DeviceBuilder withTOR(LocalDateTime timeOfRegistration) {
            this.timeOfRegistration = timeOfRegistration;
            return this;
        }

        public DeviceBuilder lastSeen(LocalDateTime lastSeen) {
            this.lastSeen = lastSeen;
            return this;
        }

        public DeviceBuilder withAccess(int access) {
            this.access = access;
            return this;
        }

        public Device build() {
            return new Device(mac, name, description, aSwitch, switchPort, portSpeed, timeOfRegistration, lastSeen, access);
        }
    }
}
