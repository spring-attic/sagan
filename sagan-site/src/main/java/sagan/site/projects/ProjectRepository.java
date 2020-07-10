package sagan.site.projects;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

	@Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.releases r LEFT JOIN FETCH p.generationsInfo g LEFT JOIN FETCH p.samples s WHERE p.id =:id")
	Project fetchFullProject(@Param("id") String id);

	@EntityGraph(value = "Project.tree", type = EntityGraph.EntityGraphType.LOAD)
	List<Project> findDistinctByStatusAndParentProjectIsNull(SupportStatus status, Sort sort);

	@Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.groups WHERE p.parentProject = NULL ORDER BY p.display.sortOrder ASC")
	List<Project> findTopLevelProjectsWithGroup();
}
