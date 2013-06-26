package org.springframework.site.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;

public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findByCategoryAndDraftFalse(PostCategory category, Pageable pageable);

	Page<Post> findByBroadcastAndDraftFalse(boolean isBroadcast, Pageable pageable);

	Page<Post> findByDraftFalse(Pageable pageable);

	Post findByIdAndDraftFalse(Long id);

}
