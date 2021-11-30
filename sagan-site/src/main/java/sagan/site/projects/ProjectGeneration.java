package sagan.site.projects;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

/**
 * Group of {@link Version} released under the same Major/Minor and sharing a common support policy.
 * <p>A generation starts with its initial release date, which is the release date of the first version of that generation.
 * It is supported officially with releases (for features, bugfixes and security fixes) in two ways:
 * <ul>
 *     <li>OSS support, publicly available to the whole Spring community through official releases and issues.
 *     <li>Commercial support, only for customers. Fixes are driven by customers only, but official releases are still publicly available to all.
 * </ul>
 * <p>Each support period has an end date, which is:
 * <ul>
 *     <li>the end date calculated using {@link sagan.site.projects.support.SupportPolicy the official support policy}.
 *     <li>the end date enforced by the project team, extending the official policy.
 * </ul>
 */
@Entity
public class ProjectGeneration implements Comparable<ProjectGeneration> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private Project project;

	/**
	 * Name of the project generation.
	 * Should be expressed with a ".x version" like {@code "5.3.x"} or {@code "Name.x"}.
	 */
	private String name;

	/**
	 * Release date of the first production-ready {@link Release} for this generation, starting point for the support policies.
	 */
	private LocalDate initialReleaseDate;

	/**
	 * End date of OSS support enforced by the project team.
	 */
	private LocalDate ossSupportEnforcedEndDate;

	/**
	 * End date of OSS support as defined by the official policy.
	 */
	private LocalDate ossSupportPolicyEndDate;

	/**
	 * End date of commercial support enforced by the project team.
	 */
	private LocalDate commercialSupportEnforcedEndDate;

	/**
	 * End date of commercial support as defined by the official policy.
	 */
	private LocalDate commercialSupportPolicyEndDate;

	protected ProjectGeneration() {

	}

	public ProjectGeneration(String name, LocalDate initialReleaseDate) {
		setName(name);
		setInitialReleaseDate(initialReleaseDate);
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		Assert.hasText(name, "Generation name should not be empty");
		Assert.isTrue(name.endsWith(".x"), "Generation name should end with '.x'");
		this.name = name;
	}

	public LocalDate getInitialReleaseDate() {
		return this.initialReleaseDate;
	}

	public void setInitialReleaseDate(LocalDate initialReleaseDate) {
		if (initialReleaseDate == null) {
			throw InvalidProjectGenerationDateException.nullReleaseDate();
		}
		this.initialReleaseDate = initialReleaseDate;
	}

	public boolean isReleased() {
		return this.initialReleaseDate.isBefore(LocalDate.now());
	}

	public LocalDate getOssSupportEnforcedEndDate() {
		return this.ossSupportEnforcedEndDate;
	}

	public void setOssSupportEnforcedEndDate(LocalDate ossSupportEndDate) {
		assertAfterInitialReleaseDate(ossSupportEndDate);
		this.ossSupportEnforcedEndDate = ossSupportEndDate;
	}

	public LocalDate getOssSupportPolicyEndDate() {
		return this.ossSupportPolicyEndDate;
	}

	public void setOssSupportPolicyEndDate(LocalDate ossSupportPolicyEndDate) {
		assertAfterInitialReleaseDate(ossSupportPolicyEndDate);
		this.ossSupportPolicyEndDate = ossSupportPolicyEndDate;
	}

	/**
	 * Return the end date of the OSS (community) support.
	 * <p>Will use the date enforced by the project team, or the date calculated with the official policy.
	 */
	public LocalDate ossSupportEndDate() {
		return (this.ossSupportEnforcedEndDate != null) ? this.ossSupportEnforcedEndDate : this.ossSupportPolicyEndDate;
	}

	public LocalDate getCommercialSupportEnforcedEndDate() {
		return this.commercialSupportEnforcedEndDate;
	}

	public void setCommercialSupportEnforcedEndDate(LocalDate commercialSupportEndDate) {
		assertAfterInitialReleaseDate(commercialSupportEndDate);
		if (commercialSupportEndDate != null && this.ossSupportEnforcedEndDate != null
				&& commercialSupportEndDate.isBefore(this.ossSupportEnforcedEndDate)) {
			throw InvalidProjectGenerationDateException.commercialSupportEndsBeforeOssSupport(commercialSupportEndDate);
		}
		this.commercialSupportEnforcedEndDate = commercialSupportEndDate;
	}

	public LocalDate getCommercialSupportPolicyEndDate() {
		return this.commercialSupportPolicyEndDate;
	}

	public void setCommercialSupportPolicyEndDate(LocalDate commercialSupportPolicyEndDate) {
		assertAfterInitialReleaseDate(commercialSupportPolicyEndDate);
		this.commercialSupportPolicyEndDate = commercialSupportPolicyEndDate;
	}

	/**
	 * Return the end date of the commercial support.
	 * <p>Will use the date enforced by the project team, or the date calculated with the official policy.
	 */
	public LocalDate commercialSupportEndDate() {
		return (this.commercialSupportEnforcedEndDate != null) ? this.commercialSupportEnforcedEndDate : this.commercialSupportPolicyEndDate;
	}

	private void assertAfterInitialReleaseDate(LocalDate endOfSupportDate) {
		if (endOfSupportDate != null && !endOfSupportDate.isAfter(this.initialReleaseDate)) {
			throw InvalidProjectGenerationDateException.endOfSupportBeforeReleaseDate(endOfSupportDate);
		}
	}

	public boolean hasCommercialSupport() {
		LocalDate now = LocalDate.now();
		if (this.initialReleaseDate.isAfter(now)) {
			return false;
		}
		if (this.commercialSupportEnforcedEndDate != null) {
			return this.commercialSupportEnforcedEndDate.isAfter(now);
		}
		if (this.commercialSupportPolicyEndDate != null) {
			return this.commercialSupportPolicyEndDate.isAfter(now);
		}
		return true;
	}

	public boolean hasOssSupport() {
		LocalDate now = LocalDate.now();
		if (this.initialReleaseDate.isAfter(now)) {
			return false;
		}
		if (this.ossSupportEnforcedEndDate != null) {
			return this.ossSupportEnforcedEndDate.isAfter(now);
		}
		if (this.ossSupportPolicyEndDate != null) {
			return this.ossSupportPolicyEndDate.isAfter(now);
		}
		return true;
	}

	public boolean isInvalidOssSupportEndDate() {
		if (this.ossSupportEnforcedEndDate != null && this.ossSupportPolicyEndDate != null) {
			return this.ossSupportEnforcedEndDate.isBefore(this.ossSupportPolicyEndDate);
		}
		return false;
	}

	public boolean isInvalidCommercialSupportEndDate() {
		if (this.commercialSupportEnforcedEndDate != null && this.commercialSupportPolicyEndDate != null) {
			return this.commercialSupportEnforcedEndDate.isBefore(this.commercialSupportPolicyEndDate);
		}
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProjectGeneration that = (ProjectGeneration) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public int compareTo(ProjectGeneration other) {
		return -initialReleaseDate.compareTo(other.initialReleaseDate);
	}
}
