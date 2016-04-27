package lv.freeradiusgui.domain;

import javax.persistence.*;
import java.lang.invoke.SwitchPoint;
import java.time.LocalDateTime;

/**
 * Created by Dan on 24.11.2015.
 */
@Entity
@Table(name = "switches")
public class Switch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "switch_id")
    private int id;

    @Column(name = "mac", unique = true)
    private String mac;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String description;

    @Column(name = "ip")
    private String ip;
    //TODO: additional fields like numberOfPorts, vendor, model, year


    public Switch(String mac, String name, String description, String ip) {
        this.mac = mac;
        this.name = name;
        this.description = description;
        this.ip = ip;
    }

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Switch aSwitch = (Switch) o;

        return mac.equals(aSwitch.mac);

    }

    @Override
    public int hashCode() {
        return mac.hashCode();
    }

    public static class SwitchBuilder {

        private String mac;
        private String name;
        private String description;
        private String ip;
        
        public SwitchBuilder() {
        }

        public SwitchBuilder withMac(String mac) {
            this.mac = mac;
            return this;
        }

        public SwitchBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SwitchBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SwitchBuilder withIp(String ip) {
            this.ip = ip;
            return this;
        }


        public Switch build() {
            return new Switch(mac, name, description, ip);
        }
    }
}
