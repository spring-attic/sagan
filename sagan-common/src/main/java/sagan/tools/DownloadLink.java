package sagan.tools;

import org.springframework.util.StringUtils;

public class DownloadLink {
    private String url;
    private String fileType;
    private String fileSize;
    private String os;
    private String architecture;

    public DownloadLink(String url, String fileType, String fileSize, String os, String architecture) {
        this.url = url;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.os = os;
        this.architecture = architecture;
    }

    public String getUrl() {
        return url;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getOs() {
        return os;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getOsAndArch() {
        return StringUtils.capitalize(os) + architecture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DownloadLink link = (DownloadLink) o;

        if (!architecture.equals(link.architecture))
            return false;
        if (!fileSize.equals(link.fileSize))
            return false;
        if (!fileType.equals(link.fileType))
            return false;
        if (!os.equals(link.os))
            return false;
        if (!url.equals(link.url))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = url.hashCode();
        result = 31 * result + fileType.hashCode();
        result = 31 * result + fileSize.hashCode();
        result = 31 * result + os.hashCode();
        result = 31 * result + architecture.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DownloadLink{" + "fileType='" + fileType + '\'' + ", os='" + os + '\'' + ", architecture='"
                + architecture + '\'' + '}';
    }
}
