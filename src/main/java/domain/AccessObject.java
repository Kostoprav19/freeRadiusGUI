package domain;

/**
 * Created by Daniels on 20.10.2015..
 */

public class AccessObject {

    private long accessObjectId;
    private String name;
    private String description;
    private String platform;
    private String url;

    public long getId() {
        return accessObjectId;
    }

    public void setId(long accessObjectId) {
        this.accessObjectId = accessObjectId;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessObject that = (AccessObject) o;

        //if (accessObjectId != that.accessObjectId) return false;
        if (!getName().equals(that.getName())) return false;
        if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
            return false;
        if (getPlatform() != null ? !getPlatform().equals(that.getPlatform()) : that.getPlatform() != null)
            return false;
        return !(getUrl() != null ? !getUrl().equals(that.getUrl()) : that.getUrl() != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (accessObjectId ^ (accessObjectId >>> 32));
        result = 31 * result + getName().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getPlatform() != null ? getPlatform().hashCode() : 0);
        result = 31 * result + (getUrl() != null ? getUrl().hashCode() : 0);
        return result;
    }
}
