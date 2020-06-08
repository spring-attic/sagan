package sagan.site.projects;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

/**
 * Group of {@link Project} artifacts released with the same {@link Version}.
 * The {@link #isCurrent() current} release should be the most recent, officially supported version.
 * A {@link Project} should not have more than one {@code Release} marked as current.
 */
@Entity
@Table(name="project_release")
public class Release implements Comparable<Release> {

	/**
	 * Version placeholder to be used in templated documentation URLs
	 */
	public static final String VERSION_PLACEHOLDER = "{version}";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Project project;

	private Version version;

	@Enumerated(EnumType.STRING)
	private ReleaseStatus releaseStatus;

	private boolean isCurrent;

	@Enumerated(EnumType.STRING)
	private Repository repository;

	/**
	 * Templated reference documentation URL
	 * @see #VERSION_PLACEHOLDER
	 */
	private String refDocUrl;

	/**
	 * Templated Javadoc documentation URL
	 * @see #VERSION_PLACEHOLDER
	 */
	private String apiDocUrl;


	protected Release() {
	}

	public Release(Version version) {
		this(version, false);
	}

	public Release(Version version, boolean isCurrent) {
		Assert.notNull(version, "Version should not be null");
		setVersion(version);
		if (isCurrent) {
			Assert.isTrue(this.isGeneralAvailability(), "Only Generally Available releases can be marked as current");
		}
		this.isCurrent = isCurrent;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Version getVersion() {
		return this.version;
	}

	public ReleaseStatus getReleaseStatus() {
		return this.releaseStatus;
	}

	public boolean isCurrent() {
		return this.isCurrent;
	}

	public boolean isGeneralAvailability() {
		return this.releaseStatus == ReleaseStatus.GENERAL_AVAILABILITY;
	}

	public boolean isPreRelease() {
		return this.releaseStatus == ReleaseStatus.PRERELEASE;
	}

	public boolean isSnapshot() {
		return this.releaseStatus == ReleaseStatus.SNAPSHOT;
	}

	public Repository getRepository() {
		return this.repository;
	}

	public String getRefDocUrl() {
		return this.refDocUrl;
	}

	public String expandRefDocUrl() {
		return expandUrl(this.refDocUrl);
	}

	public String getApiDocUrl() {
		return this.apiDocUrl;
	}

	public String expandApiDocUrl() {
		return expandUrl(this.apiDocUrl);
	}

	private String expandUrl(String templatedUrl) {
		if(templatedUrl == null) {
			return "";
		}
		if (this.isCurrent) {
			return templatedUrl.replaceAll(Pattern.quote(VERSION_PLACEHOLDER), "current");
		}
		return templatedUrl.replaceAll(Pattern.quote(VERSION_PLACEHOLDER), this.version.toString());
	}

	public void setVersion(Version version) {
		Assert.notNull(version, "Version should not be null");
		this.releaseStatus = ReleaseStatus.getFromVersion(version);
		this.repository = Repository.of(this.releaseStatus);
		this.version = version;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}

	public void setRefDocUrl(String refDocUrl) {
		this.refDocUrl = refDocUrl;
	}

	public void setApiDocUrl(String apiDocUrl) {
		this.apiDocUrl = apiDocUrl;
	}


	@Override
	public int compareTo(Release other) {
		if (other == null) {
			return -1;
		}
		if (this.isCurrent && !other.isCurrent) {
			return -1;
		}
		if (!this.isCurrent && other.isCurrent) {
			return 1;
		}
		// invert Version comparator, to get most recent version first
		return - this.version.compareTo(other.version);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Release release = (Release) o;
		return version.equals(release.version);
	}

	@Override
	public int hashCode() {
		return Objects.hash(version);
	}

	@Override
	public String toString() {
		return "Release{" +
				"id=" + id +
				", version=" + version +
				", releaseStatus=" + releaseStatus +
				", isCurrent=" + isCurrent +
				", repository=" + repository +
				", refDocUrl='" + refDocUrl + '\'' +
				", apiDocUrl='" + apiDocUrl + '\'' +
				'}';
	}
}
