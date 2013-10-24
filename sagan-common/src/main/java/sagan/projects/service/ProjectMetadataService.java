package sagan.projects.service;

import sagan.projects.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectMetadataService {
    private final Map<String, List<Project>> projectCategoryMap;
    private final String ghPagesBaseUrl;
    private final List<Project> projects;

    public ProjectMetadataService(Map<String, List<Project>> projectCategoryMap, String ghPagesBaseUrl) {
        this.projectCategoryMap = projectCategoryMap;
        this.ghPagesBaseUrl = ghPagesBaseUrl;
        this.projects = new ArrayList<>();
        for (Map.Entry<String, List<Project>> projectCategory : projectCategoryMap.entrySet()) {
            this.projects.addAll(projectCategory.getValue());
        }
    }

    public List<Project> getProjectsForCategory(String category) {
        return this.projectCategoryMap.get(category);
    }

    public List<Project> getProjects() {
        return this.projects;
    }

    public Project getProject(String id) {
        for (Project project : this.projects) {
            if (project.getId().equals(id)) {
                return project;
            }
        }
        return null;
    }

    public String getGhPagesBaseUrl() {
        return ghPagesBaseUrl;
    }
}
