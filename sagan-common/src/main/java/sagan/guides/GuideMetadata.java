package sagan.guides;

import java.util.Set;

public interface GuideMetadata {

    String getTitle();

    String getSubtitle();

    Set<String> getTags();

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
