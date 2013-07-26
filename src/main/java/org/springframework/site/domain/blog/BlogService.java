package org.springframework.site.domain.blog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.blog.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BlogService {

	private PostRepository repository;
	private TeamRepository teamRepository;
	private SearchService searchService;
	private BlogPostContentRenderer postContentRenderer;
	private DateService dateService;

	private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

	private static final Log logger = LogFactory.getLog(BlogService.class);

	@Autowired
	public BlogService(PostRepository repository, BlogPostContentRenderer postContentRenderer,
			DateService dateService, TeamRepository teamRepository,
			SearchService searchService) {
		this.repository = repository;
		this.postContentRenderer = postContentRenderer;
		this.dateService = dateService;
		this.teamRepository = teamRepository;
		this.searchService = searchService;
	}

	// Query methods

	public Post getPost(Long postId) {
		Post post = this.repository.findOne(postId);
		if (post == null) {
			throw new EntityNotFoundException("Blog post not found with Id=" + postId);
		}
		return post;
	}

	public Post getPost(String title, Date createdAt) {
		return repository.findByTitleAndCreatedAt(title, createdAt);
	}

	public Post getPublishedPost(String publicSlug) {
		Post post = this.repository.findByPublicSlugAndDraftFalseAndPublishAtBefore(publicSlug,
				this.dateService.now());
		if (post == null) {
			throw new EntityNotFoundException("Blog post not found with Id=" + publicSlug);
		}
		return post;
	}

	public Page<Post> getDraftPosts(Pageable pageRequest) {
		return this.repository.findByDraftTrue(pageRequest);
	}

	public Page<Post> getPublishedPosts(Pageable pageRequest) {
		return this.repository.findByDraftFalseAndPublishAtBefore(this.dateService.now(),
				pageRequest);
	}

	public Page<Post> getScheduledPosts(Pageable pageRequest) {
		return this.repository.findByDraftFalseAndPublishAtAfter(this.dateService.now(),
				pageRequest);
	}

	public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
		return this.repository.findByCategoryAndDraftFalseAndPublishAtBefore(category,
				this.dateService.now(), pageRequest);
	}

	public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
		return this.repository.findByBroadcastAndDraftFalseAndPublishAtBefore(true,
				this.dateService.now(), pageRequest);
	}

	public Page<Post> getPublishedPostsForMember(MemberProfile profile,
			Pageable pageRequest) {
		return this.repository.findByDraftFalseAndAuthorAndPublishAtBefore(profile,
				this.dateService.now(), pageRequest);
	}

	public Page<Post> getAllPosts(Pageable pageRequest) {
		return this.repository.findAll(pageRequest);
	}

	// CRUD Operations

	public Post addPost(PostForm postForm, String authorId) {
		String content = postForm.getContent();
		Post post = new Post(postForm.getTitle(), content, postForm.getCategory());
		MemberProfile profile = this.teamRepository.findByMemberId(authorId);
		post.setAuthor(profile);

		post.setRenderedContent(this.postContentRenderer.render(content));
		post.setRenderedSummary(this.postContentRenderer.render(extractFirstParagraph(content, 500)));
		post.setBroadcast(postForm.isBroadcast());
		post.setDraft(postForm.isDraft());
		post.setPublishAt(publishDate(postForm));
		post.setCreatedAt(createdDate(postForm, dateService.now()));

		this.repository.save(post);
		saveToIndex(post);

		return post;
	}

	private Date createdDate(PostForm postForm, Date defaultDate) {
		Date createdAt = postForm.getCreatedAt();
		if (createdAt == null) {
			createdAt = defaultDate;
		}
		return createdAt;
	}

	public void updatePost(Post post, PostForm postForm) {
		String content = postForm.getContent();

		post.setTitle(postForm.getTitle());
		post.setRawContent(content);
		post.setCategory(postForm.getCategory());

		post.setRenderedContent(this.postContentRenderer.render(content));
		post.setRenderedSummary(this.postContentRenderer.render(extractFirstParagraph(content, 500)));
		post.setBroadcast(postForm.isBroadcast());
		post.setDraft(postForm.isDraft());
		post.setPublishAt(publishDate(postForm));
		post.setCreatedAt(createdDate(postForm, post.getCreatedAt()));

		this.repository.save(post);
		saveToIndex(post);
	}

	public void deletePost(Post post) {
		this.repository.delete(post);
	}

	private Date publishDate(PostForm postForm) {
		if (!postForm.isDraft() && postForm.getPublishAt() == null) {
			return this.dateService.now();
		} else {
			return postForm.getPublishAt();
		}
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

	private void saveToIndex(Post post) {
		if (post.isLiveOn(this.dateService.now())) {
			try {
				this.searchService.saveToIndex(this.mapper.map(post));
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public void reIndexAllPosts() {
		List<Post> posts = repository.findByDraftFalseAndPublishAtBefore(dateService.now());
		for (Post post : posts) {
			saveToIndex(post);
		}
	}
}
