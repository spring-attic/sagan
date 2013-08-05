package org.springframework.site.domain.projects;

public class ProjectDocumentation {

	private final String refDocUrl;
	private final String apiDocUrl;
	private Version version;

    public ProjectDocumentation(String refDocUrl, String apiDocUrl, Version version) {
		this.refDocUrl = refDocUrl;
		this.apiDocUrl = apiDocUrl;
        this.version = version;
    }

    public Version getVersion() {
        return version;
    }

    public String getVersionName() {
		return version.getVersionName();
    }

	public String getVersionShortName() {
		return version.getShortName();
	}

	public String getReleaseDisplayName() {
		return version.getReleaseDisplayName();
	}

	public boolean hasReleaseDisplayName() {
		return !version.getReleaseDisplayName().isEmpty();
	}

	public String getRefDocUrl() {
		return refDocUrl;
	}

	public String getApiDocUrl() {
		return apiDocUrl;
	}
}
