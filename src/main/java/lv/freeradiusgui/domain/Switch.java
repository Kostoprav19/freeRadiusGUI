package lv.freeradiusgui.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("switches")
public class Switch {

    @Id
    @Column("switch_id")
    private Integer id;

    @Column("mac")
    private String mac;

    @Column("name")
    private String name;

    @Column("descr")
    private String description;

    @Column("ip")
    private String ip;

    @Column("secret")
    private String secret;

    // TODO: additional fields like numberOfPorts, vendor, model, year

    public Switch(String mac, String name, String description, String ip, String secret) {
        this.mac = mac;
        this.name = name;
        this.description = description;
        this.ip = ip;
        this.secret = secret;
    }

    public Switch() {}

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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public String toString() {
        return ("Switch{"
                + "id="
                + id
                + ", mac='"
                + mac
                + '\''
                + ", name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + ", ip='"
                + ip
                + '\''
                + ", secret='"
                + secret
                + '\''
                + '}');
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
        private String secret;

        public SwitchBuilder() {}

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

        public SwitchBuilder withSecret(String secret) {
            this.secret = secret;
            return this;
        }

        public Switch build() {
            return new Switch(mac, name, description, ip, secret);
        }
    }
}
