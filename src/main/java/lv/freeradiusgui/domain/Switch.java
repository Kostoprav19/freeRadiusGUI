package lv.freeradiusgui.domain;

import javax.persistence.*;

/**
 * Created by Dan on 24.11.2015.
 */
@Entity
@Table(name = "switches")
public class Switch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "switch_id")
    private Integer id;

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

    public Switch() {
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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "Switch{" +
                "id=" + id +
                ", mac='" + mac + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Switch)) return false;

        Switch aSwitch = (Switch) o;

        if (mac != null ? !mac.equals(aSwitch.mac) : aSwitch.mac != null) return false;
        return ip != null ? ip.equals(aSwitch.ip) : aSwitch.ip == null;
    }

    @Override
    public int hashCode() {
        Integer result = mac != null ? mac.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        return result;
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
