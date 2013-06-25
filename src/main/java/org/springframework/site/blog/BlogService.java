package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.admin.PostForm;
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

	public Post addPost(PostForm postForm) {
		String content = postForm.getContent();
		Post post = new Post(postForm.getTitle(), content, postForm.getCategory());
		post.setRenderedContent(markdownService.renderToHtml(content));
		post.setRenderedSummary(markdownService.renderToHtml(extractFirstParagraph(content, 500)));
		repository.save(post);
		return post;
	}

	String extractFirstParagraph(String content, int maxLength) {
		String paragraph = content.trim();
		int paragraphBreakpoint = paragraph.indexOf("\n\n");
		if (paragraphBreakpoint > 0) {
			paragraph = paragraph.substring(0, paragraphBreakpoint);
		}
		if (paragraph.length() > maxLength) {
			int breakpoint = paragraph.lastIndexOf(" ", maxLength);
			breakpoint = (breakpoint < 0) ? maxLength : breakpoint;
			paragraph = paragraph.substring(0, breakpoint);
		}
		return paragraph;
	}

	public Post getPost(Long postId) {
		Post one = repository.findOne(postId);
		if (one == null) {
			throw new NoSuchBlogPostException("Blog post not found with Id=" + postId);
		}
		return one;
	}

	public List<Post> mostRecentPosts(Pageable pageRequest) {
		return repository.findAll(pageRequest).getContent();
	}

	public List<Post> mostRecentPosts(PostCategory category, Pageable pageRequest) {
		return repository.findByCategory(category, pageRequest).getContent();
	}

	public PaginationInfo paginationInfo(PageRequest pageRequest) {
		return new PaginationInfo(pageRequest.getPageNumber()+1, repository.count() / pageRequest.getPageSize() + 1);
	}

	public List<Post> mostRecentBroadcastPosts(Pageable pageRequest) {
		return repository.findByBroadcast(true, pageRequest).getContent();
	}
}
