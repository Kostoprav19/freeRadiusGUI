package lv.freeradiusgui.domain;

import org.hibernate.annotations.Type;
import org.hibernate.mapping.Array;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Created by daniels on 24.11.2015.
 */
@Entity
@Table(name = "devices")
public class Device implements Serializable{

    public static final String TYPE_COMPUTER = "Computer";
    public static final String TYPE_PRINTER = "Printer";
    public static final String TYPE_OTHER = "Other";
    public static final List TYPE_ALL = Arrays.asList("Computer", "Printer", "Other");
    public static final int ACCESS_ACCEPT = 1;
    public static final int ACCESS_REJECT = 0;
    public static final int FULL_DUPLEX = 1;
    public static final int HALF_DUPLEX = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Integer id;

    @Column(name = "mac", unique = true)
    private String mac;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String description;

    @Column(name = "type")
    private String type;

    @ManyToOne(cascade={CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name="switch_id")
    private Switch aSwitch;

    @Column(name = "port")
    private Integer switchPort;

    @Column(name = "speed")
    private Integer portSpeed;

    @Column(name = "duplex")
    private Integer duplex;

    @Column(name = "tor")
    @Type(type = "lv.freeradiusgui.utils.CustomLocalDateTime")
    @DateTimeFormat(pattern="dd.MM.yyyy HH:mm")
    private LocalDateTime timeOfRegistration;

    @Column(name = "lastseen")
    @Type(type = "lv.freeradiusgui.utils.CustomLocalDateTime")
    @DateTimeFormat(pattern="dd.MM.yyyy HH:mm")
    private LocalDateTime lastSeen;

    @Column(name = "access")
    private Integer access;

    public Device(){

    }

    public Device(String mac, String name, String description, String type, Switch aSwitch, Integer switchPort, Integer portSpeed, Integer duplex, LocalDateTime timeOfRegistration, LocalDateTime lastSeen, Integer access) {
        this.mac = mac;
        this.name = name;
        this.description = description;
        this.type = type;
        this.aSwitch = aSwitch;
        this.switchPort = switchPort;
        this.portSpeed = portSpeed;
        this.duplex = duplex;
        this.timeOfRegistration = timeOfRegistration;
        this.lastSeen = lastSeen;
        this.access = access;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", aSwitch=" + aSwitch +
                ", switchPort=" + switchPort +
                ", portSpeed=" + portSpeed +
                ", timeOfRegistration=" + timeOfRegistration +
                ", lastSeen=" + lastSeen +
                ", access=" + access +
                '}';
    }

    public void setMac(String mac) {
        mac = mac.replaceAll("[^a-fA-F0-9]", ""); //normalize
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getDuplex() {
        return duplex;
    }

    public void setDuplex(Integer duplex) {
        this.duplex = duplex;
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

    public Integer getAccess() {
        return access;
    }

    public void setAccess(Integer access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Device)) return false;

        Device device = (Device) o;

        return mac != null ? mac.equals(device.mac) : device.mac == null;

    }

    @Override
    public int hashCode() {
        return mac != null ? mac.hashCode() : 0;
    }

    public static class DeviceBuilder {

        private String mac;
        private String name;
        private String description;
        private String type;
        private Switch aSwitch;
        private Integer switchPort;
        private Integer portSpeed;
        private Integer duplex;
        private LocalDateTime timeOfRegistration;
        private LocalDateTime lastSeen;
        private Integer access;

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

        public DeviceBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public DeviceBuilder onSwitch(Switch aSwitch) {
            this.aSwitch = aSwitch;
            return this;
        }

        public DeviceBuilder onPort(Integer switchPort) {
            this.switchPort = switchPort;
            return this;
        }

        public DeviceBuilder withPortSpeed(Integer portSpeed) {
            this.portSpeed = portSpeed;
            return this;
        }

        public DeviceBuilder withDuplex(Integer duplex) {
            this.duplex = duplex;
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

        public DeviceBuilder withAccess(Integer access) {
            this.access = access;
            return this;
        }

        public Device build() {
            return new Device(mac, name, description, type, aSwitch, switchPort, portSpeed, duplex, timeOfRegistration, lastSeen, access);
        }
    }
}
