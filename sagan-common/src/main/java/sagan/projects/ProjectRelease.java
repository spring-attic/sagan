package sagan.projects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

public class ProjectRelease implements Comparable<ProjectRelease> {

    private static final Pattern VERSION_DISPLAY_REGEX = Pattern.compile("([0-9.]+)\\.(RC\\d+|M\\d+)?");
    private static final Pattern PREREALSE_PATTERN = Pattern.compile("[0-9.]+(M|RC)\\d+");
    private static final Pattern SNAPSHOT_PATTERN = Pattern.compile("[0-9.].*(SNAPSHOT)");

    public enum ReleaseStatus {
        SNAPSHOT, PRERELEASE, GENERAL_AVAILABILITY;

        public static ReleaseStatus getFromVersion(String version) {
            Assert.notNull(version, "Version must not be null");
            if (PREREALSE_PATTERN.matcher(version).matches()) {
                return PRERELEASE;
            }
            if (SNAPSHOT_PATTERN.matcher(version).matches()) {
                return SNAPSHOT;
            }
            return GENERAL_AVAILABILITY;
        }
    }

    private final String versionName;
    private final ReleaseStatus releaseStatus;
    private final boolean isCurrent;
    private final String refDocUrl;
    private final String apiDocUrl;
    private final String groupId;
    private final String artifactId;
    private final ProjectRepository repository;

    public ProjectRelease(String versionName, ReleaseStatus releaseStatus, boolean isCurrent, String refDocUrl,
                          String apiDocUrl, String groupId, String artifactId) {
        this.versionName = versionName;
        this.releaseStatus = releaseStatus;
        this.isCurrent = isCurrent;
        this.refDocUrl = refDocUrl;
        this.apiDocUrl = apiDocUrl;
        this.groupId = groupId;
        this.artifactId = artifactId;
        repository = ProjectRepository.get(versionName);
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public boolean isGeneralAvailability() {
        return releaseStatus == ReleaseStatus.GENERAL_AVAILABILITY;
    }

    public boolean isPreRelease() {
        return releaseStatus == ReleaseStatus.PRERELEASE;
    }

    public boolean isSnapshot() {
        return releaseStatus == ReleaseStatus.SNAPSHOT;
    }

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }

    public String getVersion() {
        return versionName;
    }

    public String getVersionDisplayName() {
        return getVersionDisplayName(true);
    }

    public String getVersionDisplayName(boolean includePreReleaseDescription) {
        Matcher matcher = VERSION_DISPLAY_REGEX.matcher(versionName);
        matcher.find();
        String versionNumber = matcher.group(1);
        String preReleaseDescription = matcher.group(2);

        if (preReleaseDescription != null && includePreReleaseDescription) {
            return versionNumber + " " + preReleaseDescription;
        }
        return versionNumber;
    }

    public String getRefDocUrl() {
        return refDocUrl;
    }

    public boolean hasRefDocUrl() {
        return !refDocUrl.isEmpty();
    }

    public String getApiDocUrl() {
        return apiDocUrl;
    }

    public boolean hasApiDocUrl() {
        return !apiDocUrl.isEmpty();
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public ProjectRepository getRepository() {
        return repository;
    }

    @Override
    public int compareTo(ProjectRelease other) {
        return versionName.compareTo(other.versionName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProjectRelease that = (ProjectRelease) o;

        if (!apiDocUrl.equals(that.apiDocUrl))
            return false;
        if (!refDocUrl.equals(that.refDocUrl))
            return false;
        if (releaseStatus != that.releaseStatus)
            return false;
        if (!versionName.equals(that.versionName))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = versionName.hashCode();
        result = 31 * result + releaseStatus.hashCode();
        result = 31 * result + refDocUrl.hashCode();
        result = 31 * result + apiDocUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProjectRelease{" + "versionName='" + versionName + '\'' + ", release=" + releaseStatus
                + ", refDocUrl='" + refDocUrl + '\'' + ", apiDocUrl='" + apiDocUrl + '\'' + '}';
    }

}
