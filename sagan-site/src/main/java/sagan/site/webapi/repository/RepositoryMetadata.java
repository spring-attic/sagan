package sagan.site.webapi.repository;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

/**
 * 
 */
@Relation(collectionRelation = "repositories")
public class RepositoryMetadata extends ResourceSupport {

	private String identifier;

	private String name;

	private String url;

	private boolean snapshotsEnabled;

	public RepositoryMetadata() {
	}

	public RepositoryMetadata(String identifier, String name, String url, boolean snapshotsEnabled) {
		this.identifier = identifier;
		this.name = name;
		this.url = url;
		this.snapshotsEnabled = snapshotsEnabled;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isSnapshotsEnabled() {
		return snapshotsEnabled;
	}

	public void setSnapshotsEnabled(boolean snapshotsEnabled) {
		this.snapshotsEnabled = snapshotsEnabled;
	}
}
