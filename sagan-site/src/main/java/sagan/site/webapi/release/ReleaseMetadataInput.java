package sagan.site.webapi.release;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.URL;

public class ReleaseMetadataInput {

	@NotBlank
	private final String version;

	@URL
	private final String referenceDocUrl;

	@URL
	private final String apiDocUrl;

	@JsonCreator
	public ReleaseMetadataInput(@JsonProperty("version") String version,
			@JsonProperty("referenceDocUrl") String referenceDocUrl, @JsonProperty("apiDocUrl") String apiDocUrl) {
		this.version = version;
		this.referenceDocUrl = referenceDocUrl;
		this.apiDocUrl = apiDocUrl;
	}

	public String getVersion() {
		return this.version;
	}

	public String getReferenceDocUrl() {
		return this.referenceDocUrl;
	}

	public String getApiDocUrl() {
		return this.apiDocUrl;
	}

}
