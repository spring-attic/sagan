package org.springframework.site.guides;

public class GettingStartedGuide {
	private final String content;
	private final String sidebar;
	private final String guideId;
	private final static String REPO_ZIP_URL = "https://github.com/springframework-meta/gs-%s/archive/master.zip";
	private final static String REPO_HTTPS_URL = "https://github.com/springframework-meta/gs-%s.git";
	private final static String REPO_SSH_URL = "git@github.com:springframework-meta/gs-%s.git";
	private final static String REPO_SUBVERSION_URL = "https://github.com/springframework-meta/gs-%s";
	private final static String CI_STATUS_IMAGE_URL = "https://drone.io/github.com/springframework-meta/gs-%s/status.png";
	private final static String CI_LATEST_URL = "https://drone.io/github.com/springframework-meta/gs-%s/latest";

	public GettingStartedGuide(String guideId, String content, String sidebar) {
		this.content = content;
		this.sidebar = sidebar;
		this.guideId = guideId;
	}

	public String getContent() {
		return content;
	}

	public String getSidebar() {
		return sidebar;
	}

	public String getGuideId() {
		return guideId;
	}

	public String getGitRepoHttpsUrl() {
		return String.format(REPO_HTTPS_URL, guideId);
	}

	public String getZipUrl() {
		return String.format(REPO_ZIP_URL, guideId);
	}

	public String getGitRepoSshUrl() {
		return String.format(REPO_SSH_URL, guideId);
	}

	public String getGitRepoSubversionUrl() {
		return String.format(REPO_SUBVERSION_URL, guideId);
	}

	public String getCiStatusImageUrl() {
		return String.format(CI_STATUS_IMAGE_URL, guideId);
	}

	public String getCiLatestUrl() {
		return String.format(CI_LATEST_URL, guideId);
	}
}
