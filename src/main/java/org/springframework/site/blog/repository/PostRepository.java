package org.springframework.site.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.site.blog.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
