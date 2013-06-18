package org.springframework.site.documentation;

import java.util.List;

public class Project {

	private String name;
	
	private String id;
	
	private String referencePath = "reference";
	
	private String apidocsPath = "apidocs";
	
	private String version = "current";
	
	private String githubUrl;
	private String referenceUrl;
	private String apiDocsUrl;
    private List<String> supportedVersions;

    public Project(String name, String id) {
		this.name = name;
		this.id = id;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReferencePath() {
		return referencePath;
	}

	public void setReferencePath(String referencePath) {
		this.referencePath = referencePath;
	}

	public String getApidocsPath() {
		return apidocsPath;
	}

	public void setApidocsPath(String apidocsPath) {
		this.apidocsPath = apidocsPath;
	}
	
	public String getVersions() {
		return version;
	}

	public void setVersions(String versions) {
		this.version = versions;
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public void setGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public String getReferenceUrl() {
		return referenceUrl;
	}

	public void setReferenceUrl(String referenceUrl) {
		this.referenceUrl = referenceUrl;
	}

	public String getApiDocsUrl() {
		return apiDocsUrl;
	}

	public void setApiDocsUrl(String apiDocsUrl) {
		this.apiDocsUrl = apiDocsUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getLatestReferenceUrl() {
        return substituteVersion(this.getReferenceUrl(), this.supportedVersions.get(0));
    }

    public String getLatestApiDocsUrl() {
        return substituteVersion(this.getApiDocsUrl(), this.supportedVersions.get(0));
    }

    private String substituteVersion(String urlTemplate, String version) {
        return urlTemplate.replaceAll("\\{version\\}", version);
    }
}