package sagan.guides;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface GuideMetadata extends DocumentMetadata {

    String getTitle();

    String getSubtitle();

    Set<String> getProjects();

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
