package sagan.tools;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringToolsPlatformRepository extends JpaRepository<SpringToolsPlatform, String> {

}
