package sagan.site.projects;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import org.hibernate.annotations.SortNatural;

@Embeddable
public class ProjectGenerationsInfo {

	/**
	 * Set of available {@link ProjectGeneration} sorted by their name.
	 */
	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	@SortNatural
	private SortedSet<ProjectGeneration> generations = new TreeSet<>();

	/**
	 * Last modification date of the set of generations.
	 */
	private OffsetDateTime lastModified = LocalDateTime.now().atOffset(ZoneOffset.UTC);

	public SortedSet<ProjectGeneration> getGenerations() {
		return generations;
	}

	public void setGenerations(SortedSet<ProjectGeneration> generations) {
		this.generations = generations;
	}

	public OffsetDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(OffsetDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public void recordModification() {
		this.lastModified = LocalDateTime.now().atOffset(ZoneOffset.UTC);
	}
}
