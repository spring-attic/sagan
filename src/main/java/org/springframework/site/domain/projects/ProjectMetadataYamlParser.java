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
		String refDocUrl = docsBaseUrl + projectData.get("refDocPath");
		String apiDocUrl = docsBaseUrl + projectData.get("apiDocPath");
		String repoUrl = parseRepoUrl(projectData, githubOrgBaseUrl, id);
		String siteUrl = parseSiteUrl(projectData, ghPagesBaseUrl, id);
		List<ProjectVersion> documentationList = parseProjectDocumentation(projectData, docsBaseUrl, refDocUrl, apiDocUrl);
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

	private List<ProjectVersion> parseProjectDocumentation(Map<String, Object> projectData, String docsBaseUrl, String refDocTemplate, String apiDocTemplate) {
		List<ProjectVersion> projectVersions = new ArrayList<>();
		if (projectData.containsKey("supportedVersions")) {
			List versionList = getOrderedVersionList(projectData);

			Version currentVersion = null;
			for (Object value : versionList) {

				String projectRefDocTemplate = refDocTemplate;
				String projectApiDocTemplate = apiDocTemplate;
				String versionName;
				if (value instanceof String) {
					versionName = value.toString();
				} else {
					Map<String, String> versionMap = (Map<String, String>) value;
					versionName = versionMap.get("name");
					if (versionMap.containsKey("refDocPath")) {
						projectRefDocTemplate = docsBaseUrl + versionMap.get("refDocPath");
					}
					if (versionMap.containsKey("apiDocPath")) {
						projectApiDocTemplate = docsBaseUrl + versionMap.get("apiDocPath");
					}
				}
				String refDocUrl = projectRefDocTemplate.replaceAll("\\{version\\}", versionName);
				String apiDocUrl = projectApiDocTemplate.replaceAll("\\{version\\}", versionName);

				Version version = buildVersion(versionName, currentVersion);
				if (currentVersion == null && version.isCurrent()) {
					currentVersion = version;
				}

				projectVersions.add(new ProjectVersion(refDocUrl, apiDocUrl, version));

			}
		}
		return projectVersions;
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

	private List getOrderedVersionList(Map<String, Object> projectData) {
		List versionList = (List) projectData.get("supportedVersions");

		Collections.sort(versionList, new Comparator() {
			@Override
			public int compare(Object v1, Object v2) {
				return getVersionName(v2).compareTo(getVersionName(v1));
			}

			private String getVersionName(Object o) {
				if (o instanceof Map) {
					return ((Map) o).get("name").toString();
				}
				return o.toString();
			}
		});
		return versionList;
	}

}
