package sagan.projects.service;

import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.comparator.CompoundComparator;
import org.springframework.web.util.UriTemplate;

class ProjectVersionsParser {

    private static final Comparator<? super ProjectRelease> PROJECT_RELEASE_COMPARATOR;
    static {
        CompoundComparator<ProjectRelease> comparator = new CompoundComparator<ProjectRelease>();
        comparator.addComparator(new Comparator<ProjectRelease>() {
            @Override
            public int compare(ProjectRelease o1, ProjectRelease o2) {
                return o2.getVersionDisplayName(false).compareTo(o1.getVersionDisplayName(false));
            }
        });
        comparator.addComparator(new Comparator<ProjectRelease>() {
            @Override
            public int compare(ProjectRelease o1, ProjectRelease o2) {
                return o1.getReleaseStatus().compareTo(o2.getReleaseStatus());
            }
        });
        PROJECT_RELEASE_COMPARATOR = comparator;
    }

    private final Map<String, String> variables;
    private final Map<String, String> defaultUrls;

    ProjectVersionsParser(Map<String, String> variables, Map<String, String> defaultUrls) {
        this.variables = Collections.unmodifiableMap(variables);
        this.defaultUrls = Collections.unmodifiableMap(defaultUrls);
    }

    List<ProjectRelease> parse(Map<String, Object> projectData) {
        List<SupportedVersion> orderedSupportedVersions = parseSupportedVersions(projectData);
        return buildProjectReleases(orderedSupportedVersions);
    }

    private List<SupportedVersion> parseSupportedVersions(Map<String, Object> projectData) {
        List<SupportedVersion> versions = new ArrayList<>();
        if (projectData.containsKey("supportedVersions")) {
            for (Object value : (List<?>) projectData.get("supportedVersions")) {
                versions.add(parseSupportedVersion(projectData, value));
            }
        }
        Collections.sort(versions);
        return versions;
    }

    @SuppressWarnings("unchecked")
    private SupportedVersion parseSupportedVersion(Map<String, Object> projectData, Object value) {
        if (value instanceof String) {
            return new SupportedVersion(value.toString(), projectData);
        }
        Map<String, Object> versonData = (Map<String, Object>) value;
        return new SupportedVersion((String) versonData.get("name"), projectData, versonData);
    }

    private List<ProjectRelease> buildProjectReleases(List<SupportedVersion> orderedSupportedVersions) {
        SupportedVersion currentVersion = getCurrentVersion(orderedSupportedVersions);
        List<ProjectRelease> projectReleases = new ArrayList<>();
        for (SupportedVersion supportedVersion : orderedSupportedVersions) {
            projectReleases.add(supportedVersion.asProjectRelease(currentVersion));
        }
        Collections.sort(projectReleases, PROJECT_RELEASE_COMPARATOR);
        return projectReleases;
    }

    private SupportedVersion getCurrentVersion(List<SupportedVersion> orderedSupportedVersions) {
        for (SupportedVersion version : orderedSupportedVersions) {
            if (version.releaseStatus == ReleaseStatus.GENERAL_AVAILABILITY) {
                return version;
            }
        }
        return null;
    }

    private class SupportedVersion implements Comparable<SupportedVersion> {

        private final String name;
        private final String refDocUrl;
        private final String apiDocUrl;
        private final String groupId;
        private final String artifactId;
        private final ReleaseStatus releaseStatus;
        private final Map<String, String> variables;

        public SupportedVersion(String name, Map<String, Object> projectData) {
            this(name, projectData, Collections.<String, Object> emptyMap());
        }

        public SupportedVersion(String name, Map<String, Object> projectData, Map<String, Object> versionData) {
            this.name = name;
            this.refDocUrl = getValue(projectData, versionData, "refDocUrl", "");
            this.apiDocUrl = getValue(projectData, versionData, "apiDocUrl", "");
            this.groupId = getValue(projectData, versionData, "groupId", "");
            this.artifactId = getValue(projectData, versionData, "artifactId", (String) projectData.get("id"));
            this.releaseStatus = ReleaseStatus.getFromVersion(name);
            this.variables = new HashMap<String, String>(ProjectVersionsParser.this.variables);
            this.variables.put("version", name);
        }

        private String getValue(Map<String, Object> projectData, Map<String, Object> versionData, String key,
                                String defaultValue) {
            if (versionData.containsKey(key)) {
                return (String) versionData.get(key);
            }
            if (projectData.containsKey(key)) {
                return (String) projectData.get(key);
            }
            return defaultValue;
        }

        public ProjectRelease asProjectRelease(SupportedVersion currentVersion) {
            return new ProjectRelease(this.name, this.releaseStatus, this == currentVersion, buildDocUrl(
                    this.refDocUrl, "refDocUrl"), buildDocUrl(this.apiDocUrl, "apiDocUrl"),
                    groupId.isEmpty() ? this.variables.get("groupId") : groupId, artifactId);
        }

        private String buildDocUrl(String docPath, String defaultUrlKey) {
            if (docPath.equals("NONE")) {
                return "";
            }
            if (docPath.isEmpty()) {
                docPath = ProjectVersionsParser.this.defaultUrls.get(defaultUrlKey);
            }
            String docUrl = new UriTemplate(docPath).expand(this.variables).toString();
            if (!docUrl.startsWith("http")) {
                return this.variables.get("docsBaseUrl") + docUrl;
            }
            return docUrl;
        }

        @Override
        public int compareTo(SupportedVersion o) {
            return o.name.compareTo(this.name);
        }

    }

}
