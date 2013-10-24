package sagan.projects.service;

import sagan.projects.Project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

@SuppressWarnings("rawtypes")
class ProjectMetadataYamlParser {

    public ProjectMetadataService parse(Resource projectMetadataYaml) throws IOException {
        Map metadata = (Map) new Yaml().load(projectMetadataYaml.getInputStream());
        return new ProjectMetadataService(parseProjects(metadata), parseGhPagesBaseUrl(metadata));
    }

    /** For testing convenience */
    ProjectMetadataService parse(String projectMetadataYamlPath) throws IOException {
        return parse(new DefaultResourceLoader().getResource(projectMetadataYamlPath));
    }

    private String parseGhPagesBaseUrl(Map metadata) {
        return (String) ((Map) metadata.get("variables")).get("ghPagesBaseUrl");
    }

    private Map<String, List<Project>> parseProjects(Map metadata) {
        @SuppressWarnings("unchecked")
        ProjectParser projectParser = new ProjectParser(metadata);
        @SuppressWarnings("unchecked")
        Map<String, List> projectsYaml = (Map<String, List>) metadata.get("projects");
        Map<String, List<Project>> projects = new HashMap<>();

        for (Map.Entry<String, List> entry : projectsYaml.entrySet()) {
            String category = entry.getKey();

            if (category.equals("discard")) {
                continue;
            }

            if (entry.getValue() == null) {
                projects.put(category, Collections.<Project> emptyList());
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
            @SuppressWarnings("unchecked")
            Map<String, Object> projectData = (Map<String, Object>) value;
            categoryProjects.add(projectParser.parse(projectData));
        }
        return categoryProjects;
    }

}
