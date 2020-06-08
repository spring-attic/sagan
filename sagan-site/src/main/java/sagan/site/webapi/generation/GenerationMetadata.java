package sagan.site.webapi.generation;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

/**
 * 
 */
@Relation(collectionRelation = "generations")
public class GenerationMetadata extends ResourceSupport {

	private String name;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate initialReleaseDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate ossSupportEndDate;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate commercialSupportEndDate;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getInitialReleaseDate() {
		return initialReleaseDate;
	}

	public void setInitialReleaseDate(LocalDate initialReleaseDate) {
		this.initialReleaseDate = initialReleaseDate;
	}

	public LocalDate getOssSupportEndDate() {
		return ossSupportEndDate;
	}

	public void setOssSupportEndDate(LocalDate ossSupportEndDate) {
		this.ossSupportEndDate = ossSupportEndDate;
	}

	public LocalDate getCommercialSupportEndDate() {
		return commercialSupportEndDate;
	}

	public void setCommercialSupportEndDate(LocalDate commercialSupportEndDate) {
		this.commercialSupportEndDate = commercialSupportEndDate;
	}
}
