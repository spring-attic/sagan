package sagan.site.guides;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface GuideHeader {

	String getName();

	String getRepositoryName();

	String getTitle();

	String getDescription();

	String getGithubUrl();

	String getGitUrl();

	String getSshUrl();

	String getCloneUrl();

	Set<String> getProjects();

    String getZipUrl();

    String getCiStatusImageUrl();

    String getCiLatestUrl();

}
