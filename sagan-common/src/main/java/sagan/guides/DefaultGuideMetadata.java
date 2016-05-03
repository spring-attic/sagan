package sagan.guides;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.HashSet;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DefaultGuideMetadata implements GuideMetadata {

    private final static String REPO_ZIP_URL = "https://github.com/%s/%s/archive/master.zip";
    private final static String REPO_HTTPS_URL = "https://github.com/%s/%s.git";
    private final static String GITHUB_HTTPS_URL = "https://github.com/%s/%s";
    private final static String GITHUB_ISSUES_URL = "https://github.com/%s/%s/issues";
    private final static String REPO_SSH_URL = "git@github.com:%s/%s.git";
    private final static String REPO_SUBVERSION_URL = "https://github.com/%s/%s";
    private final static String CI_STATUS_IMAGE_URL = "https://travis-ci.org/%s/%s.svg?branch=master";
    private final static String CI_LATEST_URL = "https://travis-ci.org/%s/%s";

    private String ghOrgName;
    private String guideId;
    private String repoName;
    private String description;
    private Set<String> tags;
    private String title;
    private String subtitle;

    // Only used for JSON serialization
    public DefaultGuideMetadata() {
    }

    public DefaultGuideMetadata(String ghOrgName, String guideId, String repoName, String description) {
        this(ghOrgName, guideId, repoName, description, new HashSet<>());
    }

    public DefaultGuideMetadata(String ghOrgName, String guideId, String repoName, String description, Set<String> tags) {
        this.ghOrgName = ghOrgName;
        this.guideId = guideId;
        this.repoName = repoName;
        this.description = description;
        this.tags = tags;

        String[] split = description.split("::", 2);
        title = split[0].trim();
        subtitle = (split.length > 1) ? split[1].trim() : "";
    }

    public String getGhOrgName() {
        return ghOrgName;
    }

    public String getGuideId() {
        return guideId;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    @JsonIgnore
    public String getGitRepoHttpsUrl() {
        return String.format(REPO_HTTPS_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getGithubHttpsUrl() {
        return String.format(GITHUB_HTTPS_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getZipUrl() {
        return String.format(REPO_ZIP_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getGitRepoSshUrl() {
        return String.format(REPO_SSH_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getGitRepoSubversionUrl() {
        return String.format(REPO_SUBVERSION_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getCiStatusImageUrl() {
        return String.format(CI_STATUS_IMAGE_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getCiLatestUrl() {
        return String.format(CI_LATEST_URL, ghOrgName, repoName);
    }

    @JsonIgnore
    public String getGithubIssuesUrl() { return String.format(GITHUB_ISSUES_URL, ghOrgName, repoName); }

}
