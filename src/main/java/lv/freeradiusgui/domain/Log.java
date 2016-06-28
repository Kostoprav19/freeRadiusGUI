package lv.freeradiusgui.domain;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Dan on 24.11.2015.
 */
@Entity
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer id;

    @Column(name = "mac")
    private String mac;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "mac", referencedColumnName = "mac", insertable=false, updatable=false)
    private Device device;

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
    @DateTimeFormat(pattern="dd.MM.yyyy HH:mm:ss")
    private LocalDateTime timeOfRegistration;

    @Column(name = "status")
    private Integer status; // 1 - Accept, 0 - Reject

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

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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

    public Integer getDuplex() {
        return duplex;
    }

    public void setDuplex(Integer duplex) {
        this.duplex = duplex;
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
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", mac='" + mac + '\'' +
                ", aSwitch=" + aSwitch +
                ", switchPort=" + switchPort +
                ", portSpeed=" + portSpeed +
                ", timeOfRegistration=" + timeOfRegistration +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Log log = (Log) o;

        if (mac != null ? !mac.equals(log.mac) : log.mac != null) return false;
        return timeOfRegistration != null ? timeOfRegistration.equals(log.timeOfRegistration) : log.timeOfRegistration == null;

    }

    @Override
    public int hashCode() {
        int result = mac != null ? mac.hashCode() : 0;
        result = 31 * result + (timeOfRegistration != null ? timeOfRegistration.hashCode() : 0);
        return result;
    }
}
