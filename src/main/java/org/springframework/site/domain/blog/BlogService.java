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

	private SummaryExtractor summaryExtractor;

	private static final Log logger = LogFactory.getLog(BlogService.class);

	@Autowired
	public BlogService(PostRepository repository, BlogPostContentRenderer postContentRenderer,
					   DateService dateService, TeamRepository teamRepository,
					   SearchService searchService, SummaryExtractor summaryExtractor) {
		this.repository = repository;
		this.postContentRenderer = postContentRenderer;
		this.dateService = dateService;
		this.teamRepository = teamRepository;
		this.searchService = searchService;
		this.summaryExtractor = summaryExtractor;
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

	public Page<Post> getPublishedPostsByDate(int year, int month, int day, Pageable pageRequest) {
		return this.repository.findByDate(year, month, day, pageRequest);
	}

	public Page<Post> getPublishedPostsByDate(int year, int month, Pageable pageRequest) {
		return this.repository.findByDate(year, month, pageRequest);
	}

	public Page<Post> getPublishedPostsByDate(int year, Pageable pageRequest) {
		return this.repository.findByDate(year, pageRequest);
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
		post.setCreatedAt(createdDate(postForm, dateService.now()));

		setPostProperties(postForm, content, post);

		this.repository.save(post);
		saveToIndex(post);

		return post;
	}

	private void setPostProperties(PostForm postForm, String content, Post post) {
		post.setRenderedContent(this.postContentRenderer.render(content));
		String summary = summaryExtractor.extract(post.getRenderedContent(), 500);
		post.setRenderedSummary(summary);
		post.setBroadcast(postForm.isBroadcast());
		post.setDraft(postForm.isDraft());
		post.setPublishAt(publishDate(postForm));
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
		post.setCreatedAt(createdDate(postForm, post.getCreatedAt()));

		setPostProperties(postForm, content, post);

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

	public void resummarizeAllPosts() {
		List<Post> posts = repository.findAll();
		for (Post post : posts) {
			String summary = summaryExtractor.extract(post.getRenderedContent(), 500);
			post.setRenderedSummary(summary);
			this.repository.save(post);
		}
	}

}
