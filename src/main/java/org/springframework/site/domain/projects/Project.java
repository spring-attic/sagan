package org.springframework.site.domain.projects;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String id;
	private String name;
	private String githubUrl;
	private String referenceUrl;
	private String apiDocsUrl;
    private SupportedVersions supportedVersions;
    
    public Project() {
	}

	public Project(String id, String name, String githubUrl, String referenceUrl, String apiDocsUrl, SupportedVersions supportedVersions) {
		this.id = id;
		this.name = name;
		this.githubUrl = githubUrl;
		this.referenceUrl = referenceUrl;
		this.apiDocsUrl = apiDocsUrl;
        this.supportedVersions = supportedVersions;
    }
	
	public String getName() {
		return name;
	}

	public String getGithubUrl() {
		return githubUrl;
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

	public void setId(String id) {
		this.id = id;
	}

	public SupportedVersions getSupportedVersions() {
		return supportedVersions;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public void setReferenceUrl(String referenceUrl) {
		this.referenceUrl = referenceUrl;
	}

	public void setApiUrl(String apiDocsUrl) {
		this.apiDocsUrl = apiDocsUrl;
	}

	public void setSupportedVersions(SupportedVersions supportedVersions) {
		this.supportedVersions = supportedVersions;
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
}