package sagan.projects;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OrderBy;

@Entity
public class Project {

    @Id
    private String id;
    private String name;
    private String repoUrl;
    private String siteUrl;
    private String category;

    @ElementCollection
    @OrderBy("versionName DESC")
    private List<ProjectRelease> releaseList = new ArrayList<>();
    private boolean isAggregator;
    private String stackOverflowTags;


    @SuppressWarnings("unused")
    private Project() {
    }

    public Project(String id, String name, String repoUrl, String siteUrl, List<ProjectRelease> releaseList,
                   boolean isAggregator, String category) {
        this.id = id;
        this.name = name;
        this.repoUrl = repoUrl;
        this.siteUrl = siteUrl;
        this.releaseList = releaseList;
        this.isAggregator = isAggregator;
        this.category = category;
    }

    public Project(String id, String name, String repoUrl, String siteUrl, List<ProjectRelease> releaseList,
                   boolean isAggregator, String category, String stackOverflowTags) {
        this(id, name, repoUrl, siteUrl, releaseList, isAggregator, category);
        this.setStackOverflowTags(stackOverflowTags);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setProjectReleases(List<ProjectRelease> releases) {
        this.releaseList = releases;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public void setReleaseList(List<ProjectRelease> releaseList) {
        this.releaseList = releaseList;
    }

    public void setAggregator(boolean isAggregator) {
        this.isAggregator = isAggregator;
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

    public boolean isAggregator() {
        return isAggregator;
    }

    public String getStackOverflowTags() {
        return stackOverflowTags;
    }

    public void setStackOverflowTags(String stackOverflowTags) {
        this.stackOverflowTags = stackOverflowTags.replaceAll(" ","");
    }

    public Set<String> getStackOverflowTagList() {
        return StringUtils.commaDelimitedListToSet(this.stackOverflowTags);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Project project = (Project) o;

        if (id != null ? !id.equals(project.id) : project.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", repoUrl='" + repoUrl + '\'' +
                ", siteUrl='" + siteUrl + '\'' +
                ", releaseList=" + releaseList +
                ", isAggregator=" + isAggregator +
                ", stackOverflowTags=" + stackOverflowTags +
                '}';
    }
}
