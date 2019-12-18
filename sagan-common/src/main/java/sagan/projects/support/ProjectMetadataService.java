package sagan.projects.support;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sagan.projects.Project;
import sagan.projects.ProjectGroup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProjectMetadataService {

	private static final Sort sortByDisplayOrderAndId = new Sort("displayOrder", "id");

    private ProjectMetadataRepository repository;
    private ProjectGroupRepository groupRepository;

    @Autowired
    public ProjectMetadataService(ProjectMetadataRepository repository,
            ProjectGroupRepository groupRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    public List<Project> getProjectsForCategory(String category) {
        return repository.findByCategory(category, sortByDisplayOrderAndId);
    }

    public List<Project> getActiveTopLevelProjects() {
    	return repository.findDistinctByCategoryAndParentProjectIsNull("active", sortByDisplayOrderAndId);
	}

    public List<Project> getProjects() {
        return repository.findAll(sortByDisplayOrderAndId);
    }

    public List<Project> getProjectsWithReleases() {
        return repository.findAllWithReleases(sortByDisplayOrderAndId);
    }

    // for testing purpose
    public Collection<Project> getProjectsInGroup(String group) {
        if (group == null || group.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findByGroup(group);
    }

    public Project addGroups(String projectId, List<ProjectGroup> groups) {
        Project project = this.getProject(projectId);
        for (ProjectGroup grp: groups) {
            ProjectGroup savedGroup = groupRepository.findByNameIgnoreCase(grp.getName());
            if (savedGroup != null) {
                project.getGroups().add(savedGroup);
            }
        }
        repository.save(project);
        return project;
    }

    public Project deleteGroups(String projectId, List<ProjectGroup> groups) {
        Project project = this.getProject(projectId);
        for (ProjectGroup grp: groups) {
            ProjectGroup savedGroup = groupRepository.findByNameIgnoreCase(grp.getName());
            if (savedGroup != null) {
                project.getGroups().remove(savedGroup);
            }
        }
        repository.save(project);
        return project;
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
