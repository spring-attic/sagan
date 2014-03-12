package sagan.tools;

public class UpdateSiteArchive {
    private String version;
    private String url;
    private String fileSize;

    public UpdateSiteArchive(String version, String url, String fileSize) {
        this.version = version;
        this.url = url;
        this.fileSize = fileSize;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UpdateSiteArchive that = (UpdateSiteArchive) o;

        if (!fileSize.equals(that.fileSize))
            return false;
        if (!url.equals(that.url))
            return false;
        if (!version.equals(that.version))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + fileSize.hashCode();
        return result;
    }
}
