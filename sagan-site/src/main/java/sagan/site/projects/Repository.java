package sagan.site.projects;

/**
 * Spring repository hosting project artifacts
 */
public enum Repository {

	SNAPSHOT("spring-snapshots", "Spring Snapshots",
			"https://repo.spring.io/snapshot", true),
	MILESTONE("spring-milestones", "Spring Milestones",
			"https://repo.spring.io/milestone", false),
	RELEASE("spring-releases", "Spring Releases",
			"https://repo.spring.io/release", false);

	private final String id;

	private final String name;

	private final String url;

	private final boolean snapshotsEnabled;

	Repository(String id, String name, String url, Boolean snapshotsEnabled) {
		this.id = id;
		this.name = name;
		this.url = url;
		this.snapshotsEnabled = snapshotsEnabled;
	}

	/**
	 * Deduce the artifact repository hosting artifact for this {@link ReleaseStatus}
	 * @param status the release status
	 * @return the artifact repository
	 */
	public static Repository of(ReleaseStatus status) {
		switch (status) {
			case GENERAL_AVAILABILITY:
				return RELEASE;
			case PRERELEASE:
				return MILESTONE;
			case SNAPSHOT:
				return SNAPSHOT;
			default:
				return RELEASE;
		}
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getUrl() {
		return this.url;
	}

	public boolean isSnapshotsEnabled() {
		return this.snapshotsEnabled;
	}
}
