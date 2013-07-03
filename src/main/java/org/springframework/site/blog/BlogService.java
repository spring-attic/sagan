package org.springframework.site.blog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.site.blog.web.EntityNotFoundException;
import org.springframework.site.services.DateService;
import org.springframework.site.services.MarkdownService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BlogService {

	private PostRepository repository;
	private MarkdownService markdownService;
	private DateService dateService;
	private ElasticsearchOperations elasticsearchTemplate;


	@Autowired
	public BlogService(PostRepository repository, MarkdownService markdownService, DateService dateService, ElasticsearchOperations elasticsearchTemplate) {
		this.repository = repository;
		this.markdownService = markdownService;
		this.dateService = dateService;
		this.elasticsearchTemplate = elasticsearchTemplate;
	}

	public Post addPost(PostForm postForm, String author) {
		String content = postForm.getContent();
		Post post = new Post(postForm.getTitle(), content, postForm.getCategory());
		post.setAuthor(author);
		post.setRenderedContent(markdownService.renderToHtml(content));
		post.setRenderedSummary(markdownService.renderToHtml(extractFirstParagraph(content, 500)));
		post.setBroadcast(postForm.isBroadcast());
		post.setDraft(postForm.isDraft());
		post.setPublishAt(publishDate(postForm));
		repository.save(post);

		updateIndex(post);
		return post;
	}

	// package private for testing purposes
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
			throw new EntityNotFoundException("Blog post not found with Id=" + postId);
		}
		return post;
	}

	public Post getPublishedPost(Long postId) {
		Post post = repository.findByIdAndDraftFalseAndPublishAtBefore(postId, dateService.now());
		if (post == null) {
			throw new EntityNotFoundException("Blog post not found with Id=" + postId);
		}
		return post;
	}

	public Page<Post> getDraftPosts(Pageable pageRequest) {
		return repository.findByDraftTrue(pageRequest);
	}

	public Page<Post> getPublishedPosts(Pageable pageRequest) {
		return repository.findByDraftFalseAndPublishAtBefore(dateService.now(), pageRequest);
	}

	public Page<Post> getScheduledPosts(Pageable pageRequest) {
		return repository.findByDraftFalseAndPublishAtAfter(dateService.now(), pageRequest);
	}

	public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
		return repository.findByCategoryAndDraftFalseAndPublishAtBefore(category, dateService.now(), pageRequest);
	}

	public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
		return repository.findByBroadcastAndDraftFalseAndPublishAtBefore(true, dateService.now(), pageRequest);
	}

	public Page<Post> getAllPosts(Pageable pageRequest) {
		return repository.findAll(pageRequest);
	}

	public void updatePost(Post post, PostForm postForm) {
		String content = postForm.getContent();

		post.setTitle(postForm.getTitle());
		post.setRawContent(content);
		post.setCategory(postForm.getCategory());

		post.setRenderedContent(markdownService.renderToHtml(content));
		post.setRenderedSummary(markdownService.renderToHtml(extractFirstParagraph(content, 500)));

		post.setBroadcast(postForm.isBroadcast());
		post.setDraft(postForm.isDraft());
		post.setPublishAt(publishDate(postForm));

		repository.save(post);

		updateIndex(post);
}

	private void updateIndex(Post post) {
		if (elasticsearchTemplate != null) {
			IndexQuery indexQuery = new IndexQuery();
			indexQuery.setId(post.getId().toString());
			indexQuery.setObject(post);
			elasticsearchTemplate.index(indexQuery);
			elasticsearchTemplate.refresh(Post.class, true);
		}
	}

	public void deletePost(Post post) {
		repository.delete(post);
	}

	private Date publishDate(PostForm postForm) {
		return !postForm.isDraft() && postForm.getPublishAt() == null ? dateService.now() : postForm.getPublishAt();
	}
}
