package sagan.guides;

public interface GuideMetadata {

    String getTitle();

    String getSubtitle();

    String getRepoName();

    String getGuideId();

    String getGitRepoHttpsUrl();

    String getGithubHttpsUrl();

    String getZipUrl();

    String getGitRepoSshUrl();

    String getGitRepoSubversionUrl();

    String getCiStatusImageUrl();

    String getCiLatestUrl();

}
