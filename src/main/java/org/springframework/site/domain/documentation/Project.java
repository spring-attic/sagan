package org.springframework.site.domain.documentation;

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

	private ProjectDocumentVersion buildDocumentVersion(String urlTemplate, SupportedVersion version) {
        String url = urlTemplate.replaceAll("\\{version\\}", version.getFullVersion());
        return new ProjectDocumentVersion(url, version);
    }

    public List<ProjectDocumentVersion> getSupportedReferenceDocumentVersions() {
        return getDocumentVersions(this.getReferenceUrl());
    }

    public List<ProjectDocumentVersion> getSupportedApiDocsDocumentVersions() {
        return getDocumentVersions(this.getApiUrl());
    }

    private List<ProjectDocumentVersion> getDocumentVersions(String urlTemplate) {
        List<ProjectDocumentVersion> documentVersions = new ArrayList<ProjectDocumentVersion>();
		for (SupportedVersion supportedVersion : supportedVersions) {
            documentVersions.add(buildDocumentVersion(urlTemplate, supportedVersion));
		}
        return documentVersions;
    }

	public String getApiAllClassesUrl() {
		return getApiUrl() + "/allclasses-frame.html";
	}
}