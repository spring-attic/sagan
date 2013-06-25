package org.springframework.site.blog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.site.blog.Post;
import org.springframework.site.blog.PostCategory;

public interface PostRepository extends JpaRepository<Post, Long> {
	Page<Post> findByCategory(PostCategory category, Pageable pageable);

	Page<Post> findByBroadcast(boolean isBroadcast, Pageable pageable);
}
