package sagan.projects.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import sagan.projects.Project;

import java.util.List;

/**
 * @author Rob Winch
 */
public class ProjectMetadataService {
    private ProjectDataRepository repository;

    public ProjectMetadataService(ProjectDataRepository repository) {
        this.repository = repository;
    }

    public List<Project> getProjectsForCategory(String category) {
        return repository.findByCategory(category);
    }

    public List<Project> getProjects() {
        return repository.findAll(new Sort("id"));
    }

    public List<Project> getProjectsWithReleases() {
        return repository.findAllWithReleases(new Sort("id"));
    }

    public Project getProject(String id) {
        return repository.findOne(id);
    }

    public Project save(Project project) {
        return repository.save(project);
    }

    public void delete(String id) {
        repository.delete(id);
    }
}
