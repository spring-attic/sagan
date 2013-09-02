package io.spring.site.domain.projects;

import java.util.List;

public class Project {

    private String id;
    private String name;
    private final String repoUrl;
    private final String siteUrl;
    private final List<ProjectRelease> releaseList;

    public Project(String id,
                   String name,
                   String repoUrl,
                   String siteUrl,
                   List<ProjectRelease> releaseList) {
        this.id = id;
        this.name = name;
        this.repoUrl = repoUrl;
        this.siteUrl = siteUrl;
        this.releaseList = releaseList;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<ProjectRelease> getProjectReleases() {
        return releaseList;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public boolean hasSite() {
        return !siteUrl.isEmpty();
    }
}