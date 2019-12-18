package sagan.projects.support;

import java.util.List;

import sagan.projects.Project;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMetadataRepository extends JpaRepository<Project, String> {

	List<Project> findByCategory(String category, Sort sort);

	@Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.releaseList")
	List<Project> findAllWithReleases(Sort sort);

	@EntityGraph(value = "Project.tree", type = EntityGraph.EntityGraphType.LOAD)
	List<Project> findDistinctByCategoryAndParentProjectIsNull(String category, Sort sort);

	// to be used for testing only
	@Query(value = "select p.* from project p, project_groups grp, project_groups_rel rel \n"
			+ "where lower(grp.name) = :group \n"
			+ "and grp.id = rel.group_id \n"
			+ "and rel.project_id = p.id", nativeQuery = true)
	List<Project> findByGroup(@Param("group") String group);
}
