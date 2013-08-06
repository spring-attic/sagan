package org.springframework.site.domain.projects;

import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

public class ProjectParser {

	private final Map<String, String> variables;
	private final Map<String, String> defaultUrls;

	public ProjectParser(Map<String, Map<String, String>> metadata) {
		variables = metadata.get("variables");
		defaultUrls = metadata.get("defaultUrls");
	}

	public Project parse(Map<String, Object> projectData) {
		String id = projectData.get("id").toString();
		variables.put("id", id);
		String name = projectData.get("name").toString();
		String repoUrl = parseRepoUrl(projectData);
		String siteUrl = parseSiteUrl(projectData);
		List<ProjectVersion> documentationList = new ProjectVersionsParser(variables, defaultUrls).parse(projectData);
		variables.remove("id");
		return new Project(id, name, repoUrl, siteUrl, documentationList);
	}

	private String parseRepoUrl(Map<String, Object> projectData) {
		String repoUrl;
		if (projectData.containsKey("repoUrl")) {
			repoUrl = projectData.get("repoUrl").toString();
		} else {
			repoUrl = defaultUrls.get("repoUrl");
		}
		return new UriTemplate(repoUrl).expand(variables).toString();
	}

	private String parseSiteUrl(Map<String, Object> projectData) {
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

}
