package sagan.site.projects.admin;

public class ProjectFormMetadata {

	String id;

	String name;

	String parentProjectId;

	String repoUrl;

	String status;

	String stackOverflowTags;

	public ProjectFormMetadata() {
	}

	public ProjectFormMetadata(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentProjectId() {
		return parentProjectId;
	}

	public void setParentProjectId(String parentProjectId) {
		this.parentProjectId = parentProjectId;
	}

	public String getRepoUrl() {
		return repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStackOverflowTags() {
		return stackOverflowTags;
	}

	public void setStackOverflowTags(String stackOverflowTags) {
		this.stackOverflowTags = stackOverflowTags;
	}
}
