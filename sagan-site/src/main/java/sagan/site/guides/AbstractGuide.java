package sagan.site.guides;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import sagan.site.renderer.GuideContent;

import org.springframework.util.Assert;

abstract class AbstractGuide implements Guide {

	private static final String PUSH_TO_PWS_URL = "https://push-to.cfapps.io/";

	private GuideHeader header;

	private String content;

	private String tableOfContents;

	private String typeLabel;

	private String pushToPwsUrl;

	AbstractGuide() {
	}

	AbstractGuide(String typeLabel, GuideHeader header, GuideContent content) {
		this.typeLabel = typeLabel;
		this.header = header;
		this.content = content.getContent();
		this.tableOfContents = content.getTableOfContents();
	}

	public GuideHeader getHeader() {
		return this.header;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	@Override
	public String getTableOfContents() {
		return this.tableOfContents;
	}

	public String getTypeLabel() {
		return typeLabel;
	}

	public void setTypeLabel(String typeLabel) {
		Assert.notNull(typeLabel, "Expected label type to be populated");
		this.typeLabel = typeLabel;
	}

	// --- GuideMetadata delegate methods ---


	@Override
	@JsonIgnore
	public String getName() {
		return this.header.getName();
	}

	@Override
	@JsonIgnore
	public String getRepositoryName() {
		return this.header.getRepositoryName();
	}

	@Override
	@JsonIgnore
	public String getTitle() {
		return this.header.getTitle();
	}

	@Override
	@JsonIgnore
	public String getDescription() {
		return this.header.getDescription();
	}

	@Override
	@JsonIgnore
	public String getGithubUrl() {
		return this.header.getGithubUrl();
	}

	@Override
	@JsonIgnore
	public String getGitUrl() {
		return this.header.getGitUrl();
	}

	@Override
	@JsonIgnore
	public String getSshUrl() {
		return this.header.getSshUrl();
	}

	@Override
	@JsonIgnore
	public String getCloneUrl() {
		return this.header.getCloneUrl();
	}

	@Override
	@JsonIgnore
	public Set<String> getProjects() {
		return this.header.getProjects();
	}

	@Override
	@JsonIgnore
	public String getZipUrl() {
		return this.header.getZipUrl();
	}

	@Override
	@JsonIgnore
	public String getCiStatusImageUrl() {
		return this.header.getCiStatusImageUrl();
	}

	@Override
	@JsonIgnore
	public String getCiLatestUrl() {
		return this.header.getCiLatestUrl();
	}
}
