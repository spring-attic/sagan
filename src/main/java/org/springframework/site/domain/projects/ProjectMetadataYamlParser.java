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

		Map<String, List> projectsYaml = parseYamlIntoMap(projectMetadataYml);

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
				categoryProjects.add(buildProject(projectData));
			}
			projects.put(category, categoryProjects);
		}


		return projects;
	}

	private Map<String, List> parseYamlIntoMap(InputStream projectMetadataYml) {
		Map load = (Map) new Yaml().load(projectMetadataYml);
		return (Map) load.get("projects");
	}

	private Project buildProject(Map<String, Object> projectData) {
		Project project = new Project();
		project.setId((String) projectData.get("id"));
		project.setName((String) projectData.get("name"));
		project.setReferenceUrl((String) projectData.get("referenceUrl"));
		project.setGithubUrl((String) projectData.get("githubUrl"));
		project.setApiUrl((String) projectData.get("apiUrl"));

		if (projectData.containsKey("supportedVersions")) {
			List<String> versionStrings = new ArrayList<>();
			for (Object value : (List)projectData.get("supportedVersions")) {
				versionStrings.add(value.toString());
			}
			SupportedVersions supportedVersions = SupportedVersions.build(versionStrings);
			project.setSupportedVersions(supportedVersions);
		} else {
			project.setSupportedVersions(new SupportedVersions(Collections.<Version>emptyList()));
		}

		return project;
	}
}
