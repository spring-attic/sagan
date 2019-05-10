package sagan.site.guides;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.yaml.snakeyaml.Yaml;
import sagan.site.renderer.GuideContent;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

abstract class AbstractGuide implements Guide {

	private static final String PUSH_TO_PWS_URL = "https://push-to.cfapps.io/";

	private GuideHeader header;

	private String content;

	private String tableOfContents;

	private String typeLabel;

	private List<GuideImage> images;

	private String pushToPwsUrl;

	AbstractGuide() {
	}

	AbstractGuide(String typeLabel, GuideHeader header, GuideContent content) {
		this.typeLabel = typeLabel;
		this.header = header;
		this.content = content.getContent();
		this.tableOfContents = content.getTableOfContents();
		if (content.getImages() != null) {
			this.images = content.getImages()
					.stream()
					.map(img -> new GuideImage(img.getName(), img.getEncodedContent()))
					.collect(Collectors.toList());
		}
		else {
			this.images = Collections.emptyList();
		}
		this.pushToPwsUrl = createPushToPwsUrl(content.getPushToPwsMetadata());
	}

	@SuppressWarnings("unchecked")
	private String createPushToPwsUrl(String pushToPwsMetadata) {
		if (StringUtils.hasText(pushToPwsMetadata)) {
			Yaml yaml = new Yaml();
			Map<String, String> keys = (Map<String, String>) yaml.load(pushToPwsMetadata);
			final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(PUSH_TO_PWS_URL);
			keys.forEach(builder::queryParam);
			return builder.toUriString();
		}
		return null;
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

	public List<GuideImage> getImages() {
		return images;
	}

	public void setImages(List<GuideImage> images) {
		this.images = images;
	}

	@Override
	public Optional<byte[]> getImageContent(String imageName) {
		return this.images.stream()
				.filter(image -> imageName.equals(image.getName()))
				.findFirst()
				.map(image -> Base64.getDecoder().decode(image.getEncodedContent()));
	}

	@Override
	public String getPushToPwsUrl() {
		return pushToPwsUrl;
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
