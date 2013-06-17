package org.springframework.site.domain.blog;

import org.springframework.site.domain.blog.repository.PostRepository;

public class BlogService {

	private PostRepository repository;

	public BlogService(PostRepository repository) {
		this.repository = repository;
	}

	public Post addPost(String title, String content) {
		Post post = new Post();
		post.setTitle(title);
		post.setRawContent(content);
		repository.save(post);
		return post;
	}
}
