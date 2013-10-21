package sagan.team.service;

import sagan.team.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<MemberProfile, Long> {
    MemberProfile findById(Long id);
    MemberProfile findByGithubId(Long githubId);
    MemberProfile findByUsername(String username);

    List<MemberProfile> findByHiddenOrderByNameAsc(boolean hidden);

    @Modifying(clearAutomatically = true)
    @Query("update MemberProfile p set p.hidden = true where (p.githubId not in :ids or p.githubId = null)")
    int hideTeamMembersNotInIds(@Param("ids") List<Long> ids);

}
