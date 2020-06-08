package sagan.site.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long> {

	@Query("SELECT r FROM Release r LEFT JOIN FETCH r.project p WHERE p.id =:projectId AND r.version =:version")
	Release findRelease(@Param("projectId") String projectId, @Param("version") Version version);

	@Query("SELECT r FROM Release r LEFT JOIN FETCH r.project p WHERE p.id =:projectId AND r.isCurrent = TRUE")
	Release findCurrentRelease(@Param("projectId") String projectId);
}
