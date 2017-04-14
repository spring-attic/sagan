package sagan.projects;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

import com.google.common.collect.ImmutableMap;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import sagan.projects.support.Version;

@Embeddable
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectRelease implements Comparable<ProjectRelease> {

    private static final Pattern PRERELEASE_PATTERN = Pattern.compile("[A-Za-z0-9\\.\\-]+?(M|RC)\\d+");
    private static final String SNAPSHOT_SUFFIX = "SNAPSHOT";
    private static final String VERSION_PLACEHOLDER = "{version}";
    private static final String VERSION_PATTERN = Pattern.quote(VERSION_PLACEHOLDER);

    public enum ReleaseStatus {
        SNAPSHOT, PRERELEASE, GENERAL_AVAILABILITY;

        public static ReleaseStatus getFromVersion(String version) {
            Assert.notNull(version, "Version must not be null");
            if (version.endsWith(SNAPSHOT_SUFFIX)) {
                return SNAPSHOT;
            }
            if (PRERELEASE_PATTERN.matcher(version).matches()) {
                return PRERELEASE;
            }
            return GENERAL_AVAILABILITY;
        }
    }

    private String versionName;
    private ReleaseStatus releaseStatus;
    private boolean isCurrent;
    private String refDocUrl;
    private String apiDocUrl;
    private String groupId;
    private String artifactId;
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private ProjectRepository repository;

    public ProjectRelease() {
    }

    public ProjectRelease(String versionName, ReleaseStatus releaseStatus, boolean isCurrent, String refDocUrl,
                          String apiDocUrl, String groupId, String artifactId) {
        setVersion(versionName);
        if (releaseStatus != null) {
            this.releaseStatus = releaseStatus;
        }
        this.isCurrent = isCurrent;
        this.refDocUrl = refDocUrl;
        this.apiDocUrl = apiDocUrl;
        this.groupId = groupId;
        this.artifactId = artifactId;
        repository = ProjectRepository.get(versionName, this.releaseStatus);
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
        String versionNumber = versionName;
        String versionLabel = "";
        if (versionName.contains(".")) {
            versionNumber = versionName.substring(0, versionName.lastIndexOf("."));
            versionLabel = " " + versionName.substring(versionName.lastIndexOf(".") + 1);
            if (versionLabel.contains("SNAPSHOT") || versionLabel.equals(" RELEASE")) {
                versionLabel = "";
            }
        }
        return versionNumber + versionLabel;
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

    public void setVersion(String versionName) {
        this.releaseStatus = ReleaseStatus.getFromVersion(versionName);
        repository = ProjectRepository.get(versionName, this.releaseStatus);
        this.versionName = versionName;
    }

    public void setReleaseStatus(ReleaseStatus releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public void setRefDocUrl(String refDocUrl) {
        this.refDocUrl = refDocUrl;
    }

    public void setApiDocUrl(String apiDocUrl) {
        this.apiDocUrl = apiDocUrl;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setRepository(ProjectRepository repository) {
        this.repository = repository;
    }

    public void replaceVersionPattern() {
        String version = getVersion();
        setApiDocUrl(getApiDocUrl().replaceAll(VERSION_PATTERN, version));
        setRefDocUrl(getRefDocUrl().replaceAll(VERSION_PATTERN, version));
    }

    public ProjectRelease createWithVersionPattern() {
        String version = getVersion();
        ProjectRelease release = new ProjectRelease(version, releaseStatus, isCurrent, refDocUrl, apiDocUrl, groupId,
                artifactId);
        release.setApiDocUrl(getApiDocUrl().replaceAll(version, VERSION_PLACEHOLDER));
        release.setRefDocUrl(getRefDocUrl().replaceAll(version, VERSION_PLACEHOLDER));
        return release;
    }

    private static final Map<String, Integer> SPECIAL_MEANINGS =
            ImmutableMap.of("dev", -1, "rc", 1, "release", 2, "final", 3);

    /**
     * Adapted from Gradle's StaticVersionComparator
     */
    @Override
    public int compareTo(ProjectRelease other) {
        if(other == null) return -1;

        Version version1 = Version.build(getVersion());
        Version version2 = Version.build(other.getVersion());

        if(version1 == null && version2 == null) {
            return 0;
        }
        else if(version1 == null) {
            return -1;
        }
        else if(version2 == null) {
            return 1;
        }
        if (version1.equals(version2)) {
            return 0;
        }

        String[] parts1 = version1.getParts();
        String[] parts2 = version2.getParts();

        int i = 0;
        for (; i < parts1.length && i < parts2.length; i++) {
            if (parts1[i].equals(parts2[i])) {
                continue;
            }
            boolean is1Number = isNumber(parts1[i]);
            boolean is2Number = isNumber(parts2[i]);
            if (is1Number && !is2Number) {
                return 1;
            }
            if (is2Number && !is1Number) {
                return -1;
            }
            if (is1Number) {
                return Long.valueOf(parts1[i]).compareTo(Long.valueOf(parts2[i]));
            }
            // both are strings, we compare them taking into account special meaning
            Integer sm1 = SPECIAL_MEANINGS.get(parts1[i].toLowerCase());
            Integer sm2 = SPECIAL_MEANINGS.get(parts2[i].toLowerCase());
            if (sm1 != null) {
                sm2 = sm2 == null ? 0 : sm2;
                return sm1 - sm2;
            }
            if (sm2 != null) {
                return -sm2;
            }
            return parts1[i].compareTo(parts2[i]);
        }
        if (i < parts1.length) {
            return isNumber(parts1[i]) ? 1 : -1;
        }
        if (i < parts2.length) {
            return isNumber(parts2[i]) ? -1 : 1;
        }

        return 0;
    }

    private boolean isNumber(String str) {
        return str.matches("\\d+");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof ProjectRelease))
            return false;

        ProjectRelease that = (ProjectRelease) o;

        if (!versionName.equals(that.versionName))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return versionName.hashCode();
    }

    @Override
    public String toString() {
        return "ProjectRelease{" + "versionName='" + versionName + '\'' + ", release=" + releaseStatus
                + ", refDocUrl='" + refDocUrl + '\'' + ", apiDocUrl='" + apiDocUrl + '\'' + '}';
    }

}
