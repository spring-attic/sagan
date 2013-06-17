package org.springframework.site.domain.blog.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.site.domain.blog.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
}
