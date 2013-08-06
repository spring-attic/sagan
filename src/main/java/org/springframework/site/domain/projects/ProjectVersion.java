package org.springframework.site.domain.projects;

public class ProjectVersion {

	private final String refDocUrl;
	private final String apiDocUrl;
	private Version version;

    public ProjectVersion(String refDocUrl, String apiDocUrl, Version version) {
		this.refDocUrl = refDocUrl;
		this.apiDocUrl = apiDocUrl;
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }

	public String getVersionShortName() {
		return version.getShortName();
	}

	public boolean isCurrent() {
		return version.isCurrent();
	}

	public boolean isPreRelease() {
		return version.isPreRelease();
	}

	public String getRefDocUrl() {
		return refDocUrl;
	}

	public boolean hasRefDocUrl() {
		return !refDocUrl.isEmpty();
	}

	public String getApiDocUrl() {
		return apiDocUrl;
	}

	public boolean hasApiDocUrl() {
		return !apiDocUrl.isEmpty();
	}

}
