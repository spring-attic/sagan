package sagan.site.renderer;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GuideMetadata {

	private String name;

	private String repositoryName;

	private String title;

	private String description;

	private GuideType type;

	private String githubUrl;

	private String gitUrl;

	private String sshUrl;

	private String cloneUrl;

	private Set<String> projects;

	@JsonCreator
	public GuideMetadata(@JsonProperty("name") String name,
			@JsonProperty("repositoryName") String repositoryName, @JsonProperty("title") String title,
			@JsonProperty("description") String description, @JsonProperty("type") GuideType type,
			@JsonProperty("githubUrl") String githubUrl, @JsonProperty("gitUrl") String gitUrl,
			@JsonProperty("sshUrl") String sshUrl, @JsonProperty("cloneUrl") String cloneUrl,
			@JsonProperty("projects") Set<String> projects) {
		this.name = name;
		this.repositoryName = repositoryName;
		this.title = title;
		this.description = description;
		this.type = type;
		this.githubUrl = githubUrl;
		this.gitUrl = gitUrl;
		this.sshUrl = sshUrl;
		this.cloneUrl = cloneUrl;
		this.projects = projects;
	}

	public String getName() {
		return this.name;
	}

	public String getRepositoryName() {
		return this.repositoryName;
	}

	public String getTitle() {
		return this.title;
	}

	public String getDescription() {
		return this.description;
	}

	public GuideType getType() {
		return this.type;
	}

	public String getGithubUrl() {
		return this.githubUrl;
	}

	public String getGitUrl() {
		return this.gitUrl;
	}

	public String getSshUrl() {
		return this.sshUrl;
	}

	public String getCloneUrl() {
		return this.cloneUrl;
	}

	public Set<String> getProjects() {
		return this.projects;
	}
}
