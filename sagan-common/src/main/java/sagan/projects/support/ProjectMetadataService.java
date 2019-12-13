package sagan.projects.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.criteria.Predicate;

import sagan.projects.Project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProjectMetadataService {

	private static final Sort sortByDisplayOrderAndId = new Sort("displayOrder", "id");

    private ProjectMetadataRepository repository;

    @Autowired
    public ProjectMetadataService(ProjectMetadataRepository repository) {
        this.repository = repository;
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
        Specification<Project> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.and(cb.like(root.get("groupsTag").as(String.class)
                    ,"%"+group.toLowerCase()+"%")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return repository.findAll(spec, sortByDisplayOrderAndId);
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
