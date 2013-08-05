package org.springframework.site.domain.projects;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.site.domain.projects.Version.Release.CURRENT;
import static org.springframework.site.domain.projects.Version.Release.PRERELEASE;
import static org.springframework.site.domain.projects.Version.Release.SUPPORTED;

public class ProjectMetadataYamlParser {

	public Map<String, List<Project>> parse(InputStream projectMetadataYml) {
		Map<String, List<Project>> projects = new HashMap<>();

		Map metadata = (Map) new Yaml().load(projectMetadataYml);

		String githubOrgBaseUrl = metadata.get("githubOrgBaseUrl").toString();
		String ghPagesBaseUrl = metadata.get("ghPagesBaseUrl").toString();
		String docsBaseUrl = metadata.get("docsBaseUrl").toString();

		Map<String, List> projectsYaml = (Map) metadata.get("projects");

		for (Map.Entry<String, List> entry : projectsYaml.entrySet()) {
			String category = entry.getKey();
			if (category.equals("discard")) continue;

			List<Project> categoryProjects = new ArrayList<>();
			if (entry.getValue() == null) {
				projects.put(category, categoryProjects);
				continue;
			}

			for (Object value : entry.getValue()) {
				Map<String, Object> projectData = (Map<String, Object>) value;
				categoryProjects.add(buildProject(projectData, githubOrgBaseUrl, ghPagesBaseUrl, docsBaseUrl));
			}
			projects.put(category, categoryProjects);
		}

		return projects;
	}

	private Project buildProject(Map<String, Object> projectData, String githubOrgBaseUrl, String ghPagesBaseUrl, String docsBaseUrl) {
		String id = projectData.get("id").toString();
		String name = projectData.get("name").toString();
		String repoUrl = parseRepoUrl(projectData, githubOrgBaseUrl, id);
		String siteUrl = parseSiteUrl(projectData, ghPagesBaseUrl, id);
		List<ProjectVersion> documentationList = parseProjectDocumentation(projectData, docsBaseUrl);
		return new Project(id, name, repoUrl, siteUrl, documentationList);
	}

	private String parseRepoUrl(Map<String, Object> projectData, String githubOrgBaseUrl, String id) {
		String repoUrl;
		if (projectData.containsKey("repoUrl")) {
			repoUrl = projectData.get("repoUrl").toString();
		} else {
			repoUrl = String.format("%s/%s", githubOrgBaseUrl, id);
		}
		return repoUrl;
	}

	private String parseSiteUrl(Map<String, Object> projectData, String ghPagesBaseUrl, String id) {
		String siteUrl = null;
		if (projectData.containsKey("hasSite") && Boolean.valueOf(projectData.get("hasSite").toString())) {
			if (projectData.containsKey("siteUrl")) {
				siteUrl = projectData.get("siteUrl").toString();
			} else {
				siteUrl = String.format("%s/%s", ghPagesBaseUrl, id);
			}
		}
		return siteUrl;
	}


	private List<ProjectVersion> parseProjectDocumentation(Map<String, Object> projectData, String docsBaseUrl) {
		List<SupportedVersion> supportedVersions = parseSupportedVersions(projectData);
		orderVersions(supportedVersions);
		return buildProjectVersions(docsBaseUrl, supportedVersions);
	}

	class SupportedVersion {
		String name;
		String refDocPath;
		String apiDocPath;
	}

	private List<SupportedVersion> parseSupportedVersions(Map<String, Object> projectData) {
		List<SupportedVersion> versions = new ArrayList<>();
		if (projectData.containsKey("supportedVersions")) {

			for (Object value : (List) projectData.get("supportedVersions")) {
				SupportedVersion supportedVersion = new SupportedVersion();
				supportedVersion.refDocPath = "" + projectData.get("refDocPath");
				supportedVersion.apiDocPath = "" + projectData.get("apiDocPath");

				if (value instanceof String) {
					supportedVersion.name = value.toString();
				} else {
					Map<String, String> versionMap = (Map<String, String>) value;
					supportedVersion.name = versionMap.get("name");
					if (versionMap.containsKey("refDocPath")) {
						supportedVersion.refDocPath = versionMap.get("refDocPath");
					}
					if (versionMap.containsKey("apiDocPath")) {
						supportedVersion.apiDocPath = versionMap.get("apiDocPath");
					}
				}
				versions.add(supportedVersion);
			}
		}
		return versions;
	}

	private List<ProjectVersion> buildProjectVersions(String docsBaseUrl, List<SupportedVersion> supportedVersions) {
		List<ProjectVersion> projectVersions = new ArrayList<>();
		Version currentVersion = null;
		for (SupportedVersion supportedVersion : supportedVersions) {
			String projectRefDocTemplate = docsBaseUrl + supportedVersion.refDocPath;
			String refDocUrl = projectRefDocTemplate.replaceAll("\\{version\\}", supportedVersion.name);

			String projectApiDocTemplate = docsBaseUrl + supportedVersion.apiDocPath;
			String apiDocUrl = projectApiDocTemplate.replaceAll("\\{version\\}", supportedVersion.name);

			Version version = buildVersion(supportedVersion.name, currentVersion);
			if (currentVersion == null && version.isCurrent()) {
				currentVersion = version;
			}

			projectVersions.add(new ProjectVersion(refDocUrl, apiDocUrl, version));
		}
		return projectVersions;
	}

	private void orderVersions(List<SupportedVersion> supportedVersions) {
		Collections.sort(supportedVersions, new Comparator<SupportedVersion>() {
			@Override
			public int compare(SupportedVersion v1, SupportedVersion v2) {
				return v2.name.compareTo(v1.name);
			}
		});
	}

	private static Version buildVersion(String versionName, Version currentVersion) {
		boolean isPreRelease = versionName.matches("[0-9.]+(M|RC)\\d+");
		Version.Release release = SUPPORTED;
		if (isPreRelease) {
			release = PRERELEASE;
		} else if (currentVersion == null) {
			release = CURRENT;
		}
		return new Version(versionName, release);
	}

}
