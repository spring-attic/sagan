package org.springframework.site.domain.documentation;

public class ProjectDocumentVersion {

    private String url;
    private SupportedVersion version;

    public ProjectDocumentVersion(String url, SupportedVersion version) {
        this.url = url;
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version.getFullVersion();
    }

    public boolean isCurrent() {
        return version.isCurrent();
    }

    public String getVersionName() {
		return version.getVersionName();
    }
}
