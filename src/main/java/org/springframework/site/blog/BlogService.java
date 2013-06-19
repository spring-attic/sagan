package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.repository.PostRepository;
import org.springframework.site.services.MarkdownService;
import org.springframework.stereotype.Service;

import java.util.List;

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

	public List<Post> mostRecentPosts(Pageable pageRequest) {
		List<Post> posts = repository.findAll(pageRequest).getContent();
		if (posts.size() == 0) {
			throw new BlogPostsNotFound("No blog posts found for pageRequest " + pageRequest);
		}
		return posts;
	}
}
