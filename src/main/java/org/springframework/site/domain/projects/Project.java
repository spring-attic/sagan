package org.springframework.site.domain.projects;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String id;
	private String name;
	private String referenceUrl;
	private String apiDocsUrl;
    private SupportedVersions supportedVersions;
	private final String repoUrl;
	private final String siteUrl;

	public Project(String id, String name, String referenceUrl, String apiDocsUrl, SupportedVersions supportedVersions, String repoUrl, String siteUrl) {
		this.id = id;
		this.name = name;
		this.referenceUrl = referenceUrl;
		this.apiDocsUrl = apiDocsUrl;
        this.supportedVersions = supportedVersions;
		this.repoUrl = repoUrl;
		this.siteUrl = siteUrl;
	}
	
	public String getName() {
		return name;
	}

	public String getReferenceUrl() {
		return referenceUrl;
	}

	public String getApiUrl() {
		return apiDocsUrl;
	}

    public String getId() {
		return id;
	}

	public SupportedVersions getSupportedVersions() {
		return supportedVersions;
	}

    public List<ProjectDocumentation> getSupportedReferenceDocumentVersions() {
        return getDocumentVersions(this.getReferenceUrl());
    }

    public List<ProjectDocumentation> getSupportedApiDocsDocumentVersions() {
        return getDocumentVersions(this.getApiUrl());
    }

    private List<ProjectDocumentation> getDocumentVersions(String urlTemplate) {
        List<ProjectDocumentation> documentVersions = new ArrayList<>();
		for (Version supportedVersion : supportedVersions) {
            documentVersions.add(buildDocumentVersion(urlTemplate, supportedVersion));
		}
        return documentVersions;
    }

	private ProjectDocumentation buildDocumentVersion(String urlTemplate, Version version) {
		String url = urlTemplate.replaceAll("\\{version\\}", version.getFullVersion());
		return new ProjectDocumentation(url, version);
	}

	public String getApiAllClassesUrl() {
		return getApiUrl() + "/allclasses-frame.html";
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public boolean hasSite() {
		return siteUrl != null;
	}
}