package org.springframework.site.documentation;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String name;
	private String githubUrl;
	private String referenceUrl;
	private String apiDocsUrl;
    private List<String> supportedVersions;

	public Project(String name, String githubUrl, String referenceUrl, String apiDocsUrl, List<String> supportedVersions) {
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

	public String getApiDocsUrl() {
		return apiDocsUrl;
	}

    private ProjectDocumentVersion buildDocumentVersion(String urlTemplate, String version, boolean isCurrent) {
        String url = urlTemplate.replaceAll("\\{version\\}", version);
        return new ProjectDocumentVersion(url, version, isCurrent);
    }

    public List<ProjectDocumentVersion> getSupportedReferenceDocumentVersions() {
        return getDocumentVersions(this.getReferenceUrl());
    }

    public List<ProjectDocumentVersion> getSupportedApiDocsDocumentVersions() {
        return getDocumentVersions(this.getApiDocsUrl());
    }

    private List<ProjectDocumentVersion> getDocumentVersions(String urlTemplate) {
        List<ProjectDocumentVersion> documentVersions = new ArrayList<ProjectDocumentVersion>();
        for (int i = 0; i < this.supportedVersions.size(); ++i) {
            documentVersions.add(buildDocumentVersion(urlTemplate, this.supportedVersions.get(i), i == 0));
        }
        return documentVersions;
    }

}