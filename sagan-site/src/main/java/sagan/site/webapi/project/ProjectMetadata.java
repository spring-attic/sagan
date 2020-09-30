package sagan.site.webapi.project;

import sagan.site.projects.SupportStatus;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;


/**
 *
 */
@Relation(collectionRelation = "projects")
public class ProjectMetadata extends RepresentationModel<ProjectMetadata> {

	private String name;

	private String slug;

	private String repositoryUrl;

	private SupportStatus status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public SupportStatus getStatus() {
		return status;
	}

	public void setStatus(SupportStatus status) {
		this.status = status;
	}
}
