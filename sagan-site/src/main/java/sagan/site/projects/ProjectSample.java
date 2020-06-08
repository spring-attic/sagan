package sagan.site.projects;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Sample application showcasing project features
 */
@Entity
@Table(name="project_sample")
public class ProjectSample implements Comparable<ProjectSample> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Project project;

	private String title;

	private String description;

	private String url;

	private int sortOrder = Integer.MAX_VALUE;

	protected ProjectSample() {
	}

	public ProjectSample(String title, String url) {
		this.title = title;
		this.url = url;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return this.project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(int displayOrder) {
		this.sortOrder = displayOrder;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectSample that = (ProjectSample) o;
		return url.equals(that.url);
	}

	@Override
	public int hashCode() {
		return Objects.hash(url);
	}

	@Override
	public int compareTo(ProjectSample o) {
		return this.sortOrder - o.sortOrder;
	}
}
