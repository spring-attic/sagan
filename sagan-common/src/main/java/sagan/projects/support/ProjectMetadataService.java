package sagan.projects.support;

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

	public List<Project> getProjectsWithGroups() {
		return repository.findTopLevelProjectsWithGroup();
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

    public List<ProjectGroup> getAllGroups() {
    	return this.groupRepository.findAll();
	}
}
