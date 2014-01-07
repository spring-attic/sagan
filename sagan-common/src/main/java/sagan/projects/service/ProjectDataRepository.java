package sagan.projects.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sagan.blog.Post;
import sagan.projects.Project;

import java.util.List;

/**
 * @author Rob Winch
 */
@Repository
public interface ProjectDataRepository extends JpaRepository<Project, String> {
    List<Project> findByCategory(String category);
}
