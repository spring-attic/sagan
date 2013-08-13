package org.springframework.site.domain.guides;

public class Guide {
	private final String repoName;
	private final String guideId;
	private final String title;
	private final String subtitle;
	private final String content;
	private final String sidebar;
	private final static String REPO_ZIP_URL = "https://github.com/springframework-meta/%s/archive/master.zip";
	private final static String REPO_HTTPS_URL = "https://github.com/springframework-meta/%s.git";
	private final static String GITHUB_HTTPS_URL = "https://github.com/springframework-meta/%s";
	private final static String REPO_SSH_URL = "git@github.com:springframework-meta/%s.git";
	private final static String REPO_SUBVERSION_URL = "https://github.com/springframework-meta/%s";
	private final static String CI_STATUS_IMAGE_URL = "https://drone.io/github.com/springframework-meta/%s/status.png";
	private final static String CI_LATEST_URL = "https://drone.io/github.com/springframework-meta/%s/latest";

	public Guide(String repoName, String guideId, String title, String subtitle, String content, String sidebar) {
		this.repoName = repoName;
		this.guideId = guideId;
		this.title = title;
		this.subtitle = subtitle;
		this.content = content;
		this.sidebar = sidebar;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getGuideId() {
		return guideId;
	}

	public String getContent() {
		return content;
	}

	public String getSidebar() {
		return sidebar;
	}

	public String getGitRepoHttpsUrl() {
		return String.format(REPO_HTTPS_URL, repoName);
	}

	public String getGithubHttpsUrl() {
		return String.format(GITHUB_HTTPS_URL, repoName);
	}

	public String getZipUrl() {
		return String.format(REPO_ZIP_URL, repoName);
	}

	public String getGitRepoSshUrl() {
		return String.format(REPO_SSH_URL, repoName);
	}

	public String getGitRepoSubversionUrl() {
		return String.format(REPO_SUBVERSION_URL, repoName);
	}

	public String getCiStatusImageUrl() {
		return String.format(CI_STATUS_IMAGE_URL, repoName);
	}

	public String getCiLatestUrl() {
		return String.format(CI_LATEST_URL, repoName);
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}
}
