package sagan.guides;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;

import java.util.Set;

abstract class AbstractGuide implements Guide {

    private GuideMetadata metadata;
    private String content;
    private String sidebar;
	private String typeLabel;

    public AbstractGuide() {
    }

    public AbstractGuide(GuideMetadata metadata) {
        this.metadata = metadata;
    }

    public GuideMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(GuideMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        Assert.hasText(content, "Expected body content to be populated");
        this.content = content;
    }

    @Override
    public String getSidebar() {
        return sidebar;
    }

    public void setSidebar(String sidebar) {
        Assert.notNull(sidebar, "Expected sidebar content to be populated");
        this.sidebar = sidebar;
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
    public String getTitle() {
        return metadata.getTitle();
    }

    @Override
    @JsonIgnore
    public String getSubtitle() {
        return metadata.getSubtitle();
    }

    @Override
    @JsonIgnore
    public Set<String> getTags() {
        return metadata.getTags();
    }

    @Override
    @JsonIgnore
    public String getRepoName() {
        return metadata.getRepoName();
    }

    @Override
    @JsonIgnore
    public String getGuideId() {
        return metadata.getGuideId();
    }

    @Override
    @JsonIgnore
    public String getGitRepoHttpsUrl() {
        return metadata.getGitRepoHttpsUrl();
    }

    @Override
    @JsonIgnore
    public String getGithubHttpsUrl() {
        return metadata.getGithubHttpsUrl();
    }

    @Override
    @JsonIgnore
    public String getZipUrl() {
        return metadata.getZipUrl();
    }

    @Override
    @JsonIgnore
    public String getGitRepoSshUrl() {
        return metadata.getGitRepoSshUrl();
    }

    @Override
    @JsonIgnore
    public String getGitRepoSubversionUrl() {
        return metadata.getGitRepoSubversionUrl();
    }

    @Override
    @JsonIgnore
    public String getCiStatusImageUrl() {
        return metadata.getCiStatusImageUrl();
    }

    @Override
    @JsonIgnore
    public String getCiLatestUrl() {
        return metadata.getCiLatestUrl();
    }

    @Override
    @JsonIgnore
    public String getGithubIssuesUrl() { return metadata.getGithubIssuesUrl(); }

}
