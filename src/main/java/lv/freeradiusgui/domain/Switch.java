package lv.freeradiusgui.domain;

/**
 * Created by Dan on 24.11.2015.
 */
public class Switch {
    private int id;
    private String mac;
    private String name;
    private String description;
    private String ip;
    //TODO: additional fields like numberOfPorts, vendor, model, year

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

        return !(getMac() != null ? !getMac().equals(aSwitch.getMac()) : aSwitch.getMac() != null);

    }

    @Override
    public int hashCode() {
        int result = getMac().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getIp() != null ? getIp().hashCode() : 0);
        return result;
    }
}
