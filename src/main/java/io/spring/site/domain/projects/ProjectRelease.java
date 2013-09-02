package io.spring.site.domain.projects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectRelease implements Comparable<ProjectRelease> {

    private static final Pattern VERSION_DISPLAY_REGEX = Pattern.compile("([0-9.]+)\\.(RC\\d+|M\\d+)?");

    public enum ReleaseStatus {
        PRERELEASE, SNAPSHOT, GENERAL_AVAILABILITY;
    }

    private final String versionName;
    private final ReleaseStatus releaseStatus;
    private final boolean isCurrent;
    private final String refDocUrl;
    private final String apiDocUrl;
    private final String groupId;
    private final String artifactId;
    private final ProjectRepository repository;

    public ProjectRelease(String versionName, ReleaseStatus releaseStatus,
                          boolean isCurrent, String refDocUrl, String apiDocUrl, String groupId, String artifactId) {
        this.versionName = versionName;
        this.releaseStatus = releaseStatus;
        this.isCurrent = isCurrent;
        this.refDocUrl = refDocUrl;
        this.apiDocUrl = apiDocUrl;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.repository = ProjectRepository.get(versionName);
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public boolean isGeneralAvailability() {
        return this.releaseStatus == ReleaseStatus.GENERAL_AVAILABILITY;
    }

    public boolean isPreRelease() {
        return this.releaseStatus == ReleaseStatus.PRERELEASE;
    }

    public boolean isSnapshot() {
        return this.releaseStatus == ReleaseStatus.SNAPSHOT;
    }

    public String getVersion() {
        return this.versionName;
    }

    public String getVersionDisplayName() {
        Matcher matcher = VERSION_DISPLAY_REGEX.matcher(versionName);
        matcher.find();
        String versionNumber = matcher.group(1);
        String preReleaseDescription = matcher.group(2);

        if (preReleaseDescription != null) {
            return versionNumber + " " + preReleaseDescription;
        }
        return versionNumber;
    }

    public String getRefDocUrl() {
        return this.refDocUrl;
    }

    public boolean hasRefDocUrl() {
        return !this.refDocUrl.isEmpty();
    }

    public String getApiDocUrl() {
        return this.apiDocUrl;
    }

    public boolean hasApiDocUrl() {
        return !this.apiDocUrl.isEmpty();
    }

    public String getGroupId() {
        return this.groupId;
    }

    public String getArtifactId() {
        return this.artifactId;
    }

    public ProjectRepository getRepository() {
        return this.repository;
    }

    @Override
    public int compareTo(ProjectRelease other) {
        return this.versionName.compareTo(other.versionName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectRelease that = (ProjectRelease) o;

        if (!this.apiDocUrl.equals(that.apiDocUrl))
            return false;
        if (!this.refDocUrl.equals(that.refDocUrl))
            return false;
        if (this.releaseStatus != that.releaseStatus)
            return false;
        if (!this.versionName.equals(that.versionName))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.versionName.hashCode();
        result = 31 * result + this.releaseStatus.hashCode();
        result = 31 * result + this.refDocUrl.hashCode();
        result = 31 * result + this.apiDocUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProjectRelease{" + "versionName='" + this.versionName + '\''
                + ", release=" + this.releaseStatus + ", refDocUrl='" + this.refDocUrl
                + '\'' + ", apiDocUrl='" + this.apiDocUrl + '\'' + '}';
    }

}
