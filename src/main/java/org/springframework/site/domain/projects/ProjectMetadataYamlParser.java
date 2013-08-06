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
		Map metadata = (Map) new Yaml().load(projectMetadataYml);
		ProjectParser projectParser = new ProjectParser(metadata);
		Map<String, List> projectsYaml = (Map) metadata.get("projects");
		Map<String, List<Project>> projects = new HashMap<>();

		for (Map.Entry<String, List> entry : projectsYaml.entrySet()) {
			String category = entry.getKey();

			if (category.equals("discard")) {
				continue;
			}

			if (entry.getValue() == null) {
				projects.put(category, Collections.<Project>emptyList());
				continue;
			}

			List<Project> projectList = buildCategoryProjects(projectParser, entry);
			projects.put(category, projectList);
		}

		return projects;
	}

	private List<Project> buildCategoryProjects(ProjectParser projectParser, Map.Entry<String, List> entry) {
		List<Project> categoryProjects = new ArrayList<>();
		for (Object value : entry.getValue()) {
			Map<String, Object> projectData = (Map<String, Object>) value;
			categoryProjects.add(projectParser.parse(projectData));
		}
		return categoryProjects;
	}

}
