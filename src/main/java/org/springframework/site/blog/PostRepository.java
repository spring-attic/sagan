package org.springframework.site.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findByCategoryAndDraftFalse(PostCategory category, Pageable pageable);

	Page<Post> findByBroadcastAndDraftFalse(boolean isBroadcast, Pageable pageable);

	Page<Post> findByDraftFalse(Pageable pageable);
	Page<Post> findByDraftTrue(Pageable pageRequest);

	Post findByIdAndDraftFalse(Long id);

}
