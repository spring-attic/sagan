package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.site.services.MarkdownService;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

	private PostRepository repository;
	private MarkdownService markdownService;

	@Autowired
	public BlogService(PostRepository repository, MarkdownService markdownService) {
		this.repository = repository;
		this.markdownService = markdownService;
	}

	public Post addPost(String title, String content) {
		Post post = new Post();
		post.setTitle(title);
		post.setRawContent(content);
		post.setRenderedContent(markdownService.renderToHtml(content));
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
