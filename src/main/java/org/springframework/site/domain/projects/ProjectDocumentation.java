package org.springframework.site.domain.projects;

public class ProjectDocumentation {

    private String url;
    private Version version;

    public ProjectDocumentation(String url, Version version) {
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

	public String getShortName() {
		return version.getShortName();
	}

	public String getReleaseName() {
		return version.getReleaseName();
	}
}
