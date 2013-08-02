package org.springframework.site.domain.projects;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		SupportedVersions supportedVersions = parseSupportedVersions(projectData);
		return new Project(id, name, refDocUrl, apiDocUrl, supportedVersions, repoUrl, siteUrl);
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

	private SupportedVersions parseSupportedVersions(Map<String, Object> projectData) {
		if (projectData.containsKey("supportedVersions")) {
			List<String> versionStrings = new ArrayList<>();
			for (Object value : (List) projectData.get("supportedVersions")) {
				versionStrings.add(value.toString());
			}
			return SupportedVersions.build(versionStrings);
		}
		return new SupportedVersions(Collections.<Version>emptyList());
	}
}
