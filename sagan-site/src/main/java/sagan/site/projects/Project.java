package sagan.site.projects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.SortNatural;

import org.springframework.util.Assert;

import static java.util.stream.Collectors.toList;

/**
 * A project in the Spring portfolio.
 * This class contains information about:
 * <ul>
 * 	<li>the project coordinates and its support status</li>
 *	<li>its relationship with other projects (umbrella or sub projects)</li>
 *	<li>available releases and support for release generations</li>
 *  <li>information and documentation about the project</li>
 * </ul>
 */
@Entity
@NamedEntityGraph(name = "Project.tree", attributeNodes = @NamedAttributeNode("subProjects"))
public class Project {

	/**
	 * Project id, e.g. {@code "spring-boot"}
	 */
	@Id
	private String id;

	/**
	 * Project id, e.g. {@code "Spring Boot"}
	 */
	private String name;

	/**
	 * {@link SupportStatus} of the project
	 */
	@Enumerated(EnumType.STRING)
	private SupportStatus status = SupportStatus.ACTIVE;

	/**
	 * URL of the project source repository
	 */
	private String repoUrl;

	/**
	 * Parent project
	 */
	@ManyToOne
	private Project parentProject;

	/**
	 * Sub projects
	 */
	@OneToMany(mappedBy = "parentProject")
	@OrderBy("display.sortOrder")
	private List<Project> subProjects = new ArrayList<>();

	/**
	 * Set of available {@link Release} sorted by their version
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@SortNatural
	private SortedSet<Release> releases = new TreeSet<>();

	/**
	 * Set of available {@link ProjectGeneration}
	 */
	@AttributeOverrides({
			@AttributeOverride(name = "lastModified", column = @Column(name = "project_generations_lastmodified")),
	})
	private ProjectGenerationsInfo generationsInfo = new ProjectGenerationsInfo();

	/**
	 * Set of {@link ProjectGroup} this project belongs to
	 */
	@ManyToMany
	@JoinTable(name = "project_groups_rel",
			joinColumns = {@JoinColumn(name = "project_id")},
			inverseJoinColumns = {@JoinColumn(name = "group_id")}
	)
	private Set<ProjectGroup> groups = new HashSet<>();

	/**
	 * Information used for displaying the project on the website
	 */
	@Embedded
	private Display display = new Display();

	/**
	 * {@link MarkupDocument} containing information about specific Spring Boot configuration
	 */
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "source", column = @Column(name = "bootconfig_source")),
			@AttributeOverride(name = "html", column = @Column(name = "bootconfig_html")),
	})
	private MarkupDocument bootConfig = new MarkupDocument();

	/**
	 * {@link MarkupDocument} containing the project overview
	 */
	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "source", column = @Column(name = "overview_source")),
			@AttributeOverride(name = "html", column = @Column(name = "overview_html")),
	})
	private MarkupDocument overview = new MarkupDocument();

	/**
	 * List of {@link ProjectSample sample applications}
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@SortNatural
	private SortedSet<ProjectSample> samples = new TreeSet<>();

	/**
	 * Comma-separated list of tags used for official support on StackOverflow
	 */
	private String stackOverflowTags;

	protected Project() {
	}

	public Project(String id, String name) {
		this.id = id;
		setName(name);
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		Assert.hasText(name, "name should not be empty");
		this.name = name;
	}

	public SupportStatus getStatus() {
		return this.status;
	}

	public void setStatus(SupportStatus status) {
		this.status = status;
	}

	public String getRepoUrl() {
		return this.repoUrl;
	}

	public void setRepoUrl(String repoUrl) {
		this.repoUrl = repoUrl;
	}

	public Project getParentProject() {
		return this.parentProject;
	}

	public void setParentProject(Project parentProject) {
		this.parentProject = parentProject;
	}

	public String getParentId() {
		if (this.parentProject == null) {
			return null;
		}
		return this.parentProject.getId();
	}

	public boolean isTopLevelProject() {
		return this.parentProject == null;
	}

	public List<Project> getSubProjects() {
		return this.subProjects;
	}

	public void setSubProjects(List<Project> subProjects) {
		subProjects.forEach(project -> project.setParentProject(this));
		this.subProjects = subProjects;
	}

	public SortedSet<Release> getReleases() {
		return this.releases;
	}

	public void setReleases(SortedSet<Release> releases) {
		this.releases = releases;
	}

	public Release addRelease(Version version) {
		Release release = new Release(version);
		release.setProject(this);
		this.releases.add(release);
		return release;
	}

	public void removeRelease(Version version) {
		findRelease(version).ifPresent(release -> {
			release.setProject(null);
			this.releases.remove(release);
		});
	}

	public Optional<Release> findRelease(Version version) {
		return this.releases.stream().filter(release -> version.equals(release.getVersion())).findFirst();
	}

	public Optional<Release> getCurrentRelease() {
		return this.releases.stream()
				.filter(Release::isCurrent)
				.findFirst();
	}

	public List<Release> getNonCurrentReleases() {
		return this.releases.stream()
				.filter(release -> !release.isCurrent())
				.collect(toList());
	}

	public boolean hasReleases() {
		return !this.releases.isEmpty();
	}

	public ProjectGenerationsInfo getGenerationsInfo() {
		return this.generationsInfo;
	}

	public Optional<ProjectGeneration> findGeneration(String name) {
		return this.generationsInfo.getGenerations().stream().filter(gen -> gen.getName().equals(name)).findFirst();
	}

	public ProjectGeneration addGeneration(String name, LocalDate initialReleaseDate) {
		Assert.hasText(name, "name should not be empty");
		ProjectGeneration generation = new ProjectGeneration(name, initialReleaseDate);
		generation.setProject(this);
		this.generationsInfo.getGenerations().add(generation);
		this.generationsInfo.recordModification();
		return generation;
	}

	public void removeGeneration(String name) {
		findGeneration(name).ifPresent(generation -> {
			generation.setProject(null);
			this.generationsInfo.getGenerations().remove(generation);
			this.generationsInfo.recordModification();
		});
	}

	public boolean hasGenerations() {
		return !this.generationsInfo.getGenerations().isEmpty();
	}

	public Set<ProjectGroup> getGroups() {
		return this.groups;
	}

	public void setGroups(Set<ProjectGroup> groups) {
		this.groups = groups;
	}

	public Display getDisplay() {
		return this.display;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public MarkupDocument getBootConfig() {
		return this.bootConfig;
	}

	public void setBootConfig(MarkupDocument bootConfig) {
		this.bootConfig = bootConfig;
	}

	public MarkupDocument getOverview() {
		return this.overview;
	}

	public void setOverview(MarkupDocument overview) {
		this.overview = overview;
	}

	public SortedSet<ProjectSample> getSamples() {
		return this.samples;
	}

	public void removeSample(ProjectSample sample) {
		sample.setProject(null);
		this.samples.remove(sample);
	}

	public ProjectSample addSample(String title, String url) {
		ProjectSample projectSample = new ProjectSample(title, url);
		projectSample.setProject(this);
		this.samples.add(projectSample);
		return projectSample;
	}

	public void setSamples(SortedSet<ProjectSample> samples) {
		this.samples = samples;
	}

	public boolean hasSamples() {
		return !this.samples.isEmpty();
	}

	public String getStackOverflowTags() {
		return this.stackOverflowTags;
	}

	public void setStackOverflowTags(String stackOverflowTags) {
		this.stackOverflowTags = stackOverflowTags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Project project = (Project) o;
		return name.equals(project.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
