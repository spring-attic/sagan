package sagan.site.projects;

/**
 * Support status for Spring projects
 */
public enum SupportStatus {

	/**
	 * Project is incubating and is not supported for production use
	 */
	INCUBATING("Incubating"),
	/**
	 * Project is actively supported by the Spring team
	 */
	ACTIVE("Active"),
	/**
	 * Project is actively supported by the Spring community
	 */
	COMMUNITY("Community"),
	/**
	 * Project is not supported anymore
	 */
	END_OF_LIFE("End Of Life");

	private final String label;

	SupportStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
