package sagan.projects.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import sagan.projects.ProjectGroup;
import sagan.projects.Project;

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

    public Collection<Project> getProjectsInGroup(String group) {
        if (group == null || group.isEmpty()) {
            return Collections.emptyList();
        }

        ProjectGroup grp = groupRepository.findByNameIgnoreCase(group);
        return grp != null?
                repository.findByGroups(grp, sortByDisplayOrderAndId) : Collections.emptyList();
    }

    public Project addToGroup(String projectId, List<ProjectGroup> groups) {
        Project project = this.getProject(projectId);
        List<ProjectGroup> savedGroupList = new ArrayList<>();
        for (ProjectGroup grp: groups) {
            ProjectGroup savedGroup = groupRepository.findByNameIgnoreCase(grp.getName());
            savedGroup.getProjects().add(project);
            project.getGroups().add(savedGroup);
            savedGroupList.add(savedGroup);
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
