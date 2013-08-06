package org.springframework.site.domain.projects;

import org.springframework.web.util.UriTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.site.domain.projects.ProjectVersion.Release.CURRENT;
import static org.springframework.site.domain.projects.ProjectVersion.Release.PRERELEASE;
import static org.springframework.site.domain.projects.ProjectVersion.Release.SUPPORTED;

public class ProjectMetadataYamlParser {

	public Map<String, List<Project>> parse(InputStream projectMetadataYml) {
		Map<String, List<Project>> projects = new HashMap<>();

		Map metadata = (Map) new Yaml().load(projectMetadataYml);

		Map<String, String> variables = (Map<String, String>) metadata.get("variables");
		Map<String, String> defaultUrls = (Map<String, String>) metadata.get("defaultUrls");
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
				categoryProjects.add(buildProject(projectData, variables, defaultUrls));
			}
			projects.put(category, categoryProjects);
		}

		return projects;
	}

	private Project buildProject(Map<String, Object> projectData, Map<String, String> variables, Map<String, String> defaultUrls) {
		String id = projectData.get("id").toString();
		variables.put("id", id);
		String name = projectData.get("name").toString();
		String repoUrl = parseRepoUrl(projectData, variables, defaultUrls);
		String siteUrl = parseSiteUrl(projectData, variables, defaultUrls);
		List<ProjectVersion> documentationList = parseProjectDocumentation(projectData, variables, defaultUrls);
		variables.remove("id");
		return new Project(id, name, repoUrl, siteUrl, documentationList);
	}

	private String parseRepoUrl(Map<String, Object> projectData, Map<String, String> variables, Map<String, String> defaultUrls) {
		String repoUrl;
		if (projectData.containsKey("repoUrl")) {
			repoUrl = projectData.get("repoUrl").toString();
		} else {
			repoUrl = defaultUrls.get("repoUrl");
		}
		return new UriTemplate(repoUrl).expand(variables).toString();
	}

	private String parseSiteUrl(Map<String, Object> projectData, Map<String, String> variables, Map<String, String> defaultUrls) {
		String siteUrl;
		if (projectData.containsKey("siteUrl")) {
			siteUrl = projectData.get("siteUrl").toString();
			if (siteUrl.equals("NONE")) {
				return "";
			}
		} else {
			siteUrl = defaultUrls.get("siteUrl");
		}
		return new UriTemplate(siteUrl).expand(variables).toString();
	}

	private List<ProjectVersion> parseProjectDocumentation(Map<String, Object> projectData, Map<String, String> variables, Map<String, String> defaultUrls) {
		List<SupportedVersion> supportedVersions = parseSupportedVersions(projectData);
		orderVersions(supportedVersions);
		return buildProjectVersions(supportedVersions, variables, defaultUrls);
	}

	class SupportedVersion {
		String name;
		String refDocUrl;
		String apiDocUrl;
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

	private List<ProjectVersion> buildProjectVersions(List<SupportedVersion> supportedVersions, Map<String, String> variables, Map<String, String> defaultUrls) {
		List<ProjectVersion> projectVersions = new ArrayList<>();
		String currentVersion = null;
		for (SupportedVersion supportedVersion : supportedVersions) {
			variables.put("version", supportedVersion.name);
			String refDocUrl = buildDocUrl(supportedVersion.refDocUrl, variables, defaultUrls, "refDocUrl");
			String apiDocUrl = buildDocUrl(supportedVersion.apiDocUrl, variables, defaultUrls, "apiDocUrl");
			variables.remove("version");
			ProjectVersion.Release release = getVersionRelease(supportedVersion.name, currentVersion);
			if (currentVersion == null && release == CURRENT) {
				currentVersion = supportedVersion.name;
			}
			projectVersions.add(new ProjectVersion(supportedVersion.name, release, refDocUrl, apiDocUrl));
		}
		return projectVersions;
	}

	private String buildDocUrl(String docPath, Map<String, String> variables, Map<String, String> defaultUrls, String defaultUrlKey) {
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

	private ProjectVersion.Release getVersionRelease(String versionName, String currentVersion) {
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
