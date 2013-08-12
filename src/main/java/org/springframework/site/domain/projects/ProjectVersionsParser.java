package org.springframework.site.domain.projects;

import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.springframework.site.domain.projects.ProjectRelease.ReleaseStatus.CURRENT;
import static org.springframework.site.domain.projects.ProjectRelease.ReleaseStatus.PRERELEASE;
import static org.springframework.site.domain.projects.ProjectRelease.ReleaseStatus.SUPPORTED;

class ProjectVersionsParser {

	private final Map<String, String> variables;
	private final Map<String, String> defaultUrls;

	private class SupportedVersion {
		String name;
		String refDocUrl;
		String apiDocUrl;
	}

	ProjectVersionsParser(Map<String, String> variables, Map<String, String> defaultUrls) {
		this.variables = variables;
		this.defaultUrls = defaultUrls;
	}

	List<ProjectRelease> parse(Map<String, Object> projectData) {
		List<SupportedVersion> supportedVersions = parseSupportedVersions(projectData);
		orderVersions(supportedVersions);
		return buildProjectVersions(supportedVersions);
	}

	private List<SupportedVersion> parseSupportedVersions(Map<String, Object> projectData) {
		List<SupportedVersion> versions = new ArrayList<>();
		if (projectData.containsKey("supportedVersions")) {
			String projectRefDocUrl = "";
			if (projectData.containsKey("refDocUrl")) {
				projectRefDocUrl = (String) projectData.get("refDocUrl");
			}
			String projectApiDocUrl = "";
			if (projectData.containsKey("apiDocUrl")) {
				projectApiDocUrl = (String) projectData.get("apiDocUrl");
			}

			for (Object value : (List) projectData.get("supportedVersions")) {
				SupportedVersion supportedVersion = new SupportedVersion();
				supportedVersion.refDocUrl = projectRefDocUrl;
				supportedVersion.apiDocUrl = projectApiDocUrl;

				if (value instanceof String) {
					supportedVersion.name = value.toString();
				} else {
					Map<String, String> versionMap = (Map<String, String>) value;
					supportedVersion.name = versionMap.get("name");
					if (versionMap.containsKey("refDocUrl")) {
						supportedVersion.refDocUrl = versionMap.get("refDocUrl");
					}
					if (versionMap.containsKey("apiDocUrl")) {
						supportedVersion.apiDocUrl = versionMap.get("apiDocUrl");
					}
				}
				versions.add(supportedVersion);
			}
		}
		return versions;
	}

	private void orderVersions(List<SupportedVersion> supportedVersions) {
		Collections.sort(supportedVersions, new Comparator<SupportedVersion>() {
			@Override
			public int compare(SupportedVersion v1, SupportedVersion v2) {
				return v2.name.compareTo(v1.name);
			}
		});
	}

	private List<ProjectRelease> buildProjectVersions(List<SupportedVersion> supportedVersions) {
		List<ProjectRelease> projectReleases = new ArrayList<>();
		String currentVersion = null;
		for (SupportedVersion supportedVersion : supportedVersions) {
			variables.put("version", supportedVersion.name);
			String refDocUrl = buildDocUrl(supportedVersion.refDocUrl, "refDocUrl");
			String apiDocUrl = buildDocUrl(supportedVersion.apiDocUrl, "apiDocUrl");
			variables.remove("version");
			ProjectRelease.ReleaseStatus releaseStatus = getVersionRelease(supportedVersion.name, currentVersion);
			if (currentVersion == null && releaseStatus == CURRENT) {
				currentVersion = supportedVersion.name;
			}
			projectReleases.add(new ProjectRelease(supportedVersion.name, releaseStatus, refDocUrl, apiDocUrl));
		}
		return projectReleases;
	}

	private String buildDocUrl(String docPath, String defaultUrlKey) {
		if (docPath.equals("NONE")) {
			return "";
		}

		if (docPath.isEmpty()) {
			docPath = defaultUrls.get(defaultUrlKey);
		}

		String docUrl = new UriTemplate(docPath).expand(variables).toString();
		if (!docUrl.startsWith("http")) {
			return variables.get("docsBaseUrl") + docUrl;
		}
		return docUrl;
	}

	private ProjectRelease.ReleaseStatus getVersionRelease(String versionName, String currentVersion) {
		boolean isPreRelease = versionName.matches("[0-9.]+(M|RC)\\d+");
		if (isPreRelease) {
			return PRERELEASE;
		} else if (currentVersion == null) {
			return CURRENT;
		} else {
			return SUPPORTED;
		}
	}
}
