package org.springframework.site.blog.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.site.blog.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
}
