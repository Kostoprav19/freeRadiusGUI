package lv.freeradiusgui.domain;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

@Table("logs")
public class Log {

    public static final int STATUS_ACCEPT = 1;
    public static final int STATUS_REJECT = 0;

    @Id
    @Column("log_id")
    private Integer id;

    @Column("mac")
    private String mac;

    @Transient private Device device;

    @Column("switch_id")
    private AggregateReference<Switch, Integer> switchRef;

    @Transient private Switch aSwitch;

    @Column("port")
    private Integer switchPort;

    @Column("speed")
    private Integer portSpeed;

    @Column("duplex")
    private Integer duplex;

    @Column("tor")
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime timeOfRegistration;

    @Column("status")
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
        this.mac = (mac == null) ? null : mac.replaceAll("[^a-fA-F0-9]", "");
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
        this.switchRef =
                (aSwitch == null || aSwitch.getId() == null)
                        ? null
                        : AggregateReference.to(aSwitch.getId());
    }

    public Integer getSwitchId() {
        return switchRef == null ? null : switchRef.getId();
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
        return ("Log{"
                + "id="
                + id
                + ", mac='"
                + mac
                + '\''
                + ", aSwitch="
                + aSwitch
                + ", switchPort="
                + switchPort
                + ", portSpeed="
                + portSpeed
                + ", timeOfRegistration="
                + timeOfRegistration
                + ", status="
                + status
                + '}');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Log log = (Log) o;

        if (mac != null ? !mac.equals(log.mac) : log.mac != null) return false;
        return timeOfRegistration != null
                ? timeOfRegistration.equals(log.timeOfRegistration)
                : log.timeOfRegistration == null;
    }

    @Override
    public int hashCode() {
        int result = mac != null ? mac.hashCode() : 0;
        result = 31 * result + (timeOfRegistration != null ? timeOfRegistration.hashCode() : 0);
        return result;
    }
}
