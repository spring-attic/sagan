package sagan.site.renderer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GuideContent {

	private String repositoryName;

	private String tableOfContents;

	private String content;


	@JsonCreator
	public GuideContent(@JsonProperty("repositoryName") String repositoryName,
			@JsonProperty("tableOfContents") String tableOfContents,
			@JsonProperty("content") String content) {
		this.repositoryName = repositoryName;
		this.tableOfContents = tableOfContents;
		this.content = content;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public String getTableOfContents() {
		return tableOfContents;
	}

	public String getContent() {
		return content;
	}

}
