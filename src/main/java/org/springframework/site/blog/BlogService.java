package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.web.NoSuchBlogPostException;
import org.springframework.site.blog.web.ResultList;
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
		post.setBroadcast(postForm.isBroadcast());
		post.setDraft(postForm.isDraft());
		repository.save(post);
		return post;
	}

	//TODO extract this out
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
		Post post = repository.findOne(postId);
		if (post == null) {
			throw new NoSuchBlogPostException("Blog post not found with Id=" + postId);
		}
		return post;
	}

	public Post getPublishedPost(Long postId) {
		Post post = repository.findByIdAndDraftFalse(postId);
		if (post == null) {
			throw new NoSuchBlogPostException("Blog post not found with Id=" + postId);
		}
		return post;
	}

	public ResultList<Post> getPublishedPosts(Pageable pageRequest) {
		List<Post> posts = repository.findByDraftFalse(pageRequest).getContent();
		return new ResultList<Post>(posts, buildPaginationInfo(pageRequest));
	}

	public ResultList<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
		List<Post> posts = repository.findByCategoryAndDraftFalse(category, pageRequest).getContent();
		return new ResultList<Post>(posts, buildPaginationInfo(pageRequest));
	}

	public ResultList<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
		List<Post> posts = repository.findByBroadcastAndDraftFalse(true, pageRequest).getContent();
		return new ResultList<Post>(posts, buildPaginationInfo(pageRequest));
	}

	public ResultList<Post> getAllPosts(Pageable pageRequest) {
		List<Post> posts = repository.findAll(pageRequest).getContent();
		return new ResultList<Post>(posts, buildPaginationInfo(pageRequest));
	}

	private PaginationInfo buildPaginationInfo(Pageable pageRequest) {
		return new PaginationInfo(pageRequest, repository.count());
	}
}
