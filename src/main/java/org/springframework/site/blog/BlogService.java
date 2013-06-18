package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

	private PostRepository repository;

	@Autowired
	public BlogService(PostRepository repository) {
		this.repository = repository;
	}

	public Post addPost(String title, String content) {
		Post post = new Post();
		post.setTitle(title);
		post.setRawContent(content);
		post.setRenderedContent(content);
		repository.save(post);
		return post;
	}

	public Post getPost(Long postId) {
		Post one = repository.findOne(postId);
		if (one == null) {
			throw new NoSuchBlogPostException("Blog post not found with Id=" + postId);
		}
		return one;
	}
}
