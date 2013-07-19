package org.springframework.site.domain.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<MemberProfile, Long> {
	MemberProfile findByMemberId(String memberId);

	List<MemberProfile> findByHidden(boolean b);
}
