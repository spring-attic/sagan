package sagan.projects.support;

import sagan.projects.Project;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMetadataRepository extends JpaRepository<Project, String> {
    List<Project> findByCategoryOrderByNameAsc(String category);

    @Query("SELECT DISTINCT p FROM Project p JOIN FETCH p.releaseList")
    List<Project> findAllWithReleases(Sort sort);
}
