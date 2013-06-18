package org.springframework.site.documentation;

public class Project {

	private String name;
	
	private String id;
	
	private String referencePath = "reference";
	
	private String apidocsPath = "apidocs";
	
	private String version = "current";
	
	private String githubUrl;
	private String referenceUrl;
	private String apiDocsUrl;

	public Project(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public Project(String name, String githubUrl, String referenceUrl, String apiDocsUrl) {
		this.name = name;
		this.githubUrl = githubUrl;
		this.referenceUrl = referenceUrl;
		this.apiDocsUrl = apiDocsUrl;
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

}