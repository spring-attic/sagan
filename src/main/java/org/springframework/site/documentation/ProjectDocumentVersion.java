package org.springframework.site.documentation;

public class ProjectDocumentVersion {

    private String url;
    private String version;
    private boolean isCurrent;

    public ProjectDocumentVersion(String url, String version, boolean current) {
        this.url = url;
        this.version = version;
        isCurrent = current;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public String getVersionName() {
        String name = this.getVersion();
        if(this.isCurrent()) { name += " (Current)"; }
        return name;
    }
}
