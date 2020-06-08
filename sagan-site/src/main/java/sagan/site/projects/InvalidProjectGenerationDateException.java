package sagan.site.projects;

import java.time.LocalDate;

public class InvalidProjectGenerationDateException extends RuntimeException {

	private final LocalDate invalidDate;

	InvalidProjectGenerationDateException(LocalDate invalidDate, String message) {
		super(message);
		this.invalidDate = invalidDate;
	}

	static InvalidProjectGenerationDateException nullReleaseDate() {
		return new InvalidProjectGenerationDateException(null, "Initial release date should not be null");
	}

	static InvalidProjectGenerationDateException endOfSupportBeforeReleaseDate(LocalDate supportEndDate) {
		return new InvalidProjectGenerationDateException(supportEndDate, "End of support should be after initial release");
	}

	static InvalidProjectGenerationDateException commercialSupportEndsBeforeOssSupport(LocalDate supportEndDate) {
		return new InvalidProjectGenerationDateException(supportEndDate, "Commercial support should not end before OSS support.");
	}

}
