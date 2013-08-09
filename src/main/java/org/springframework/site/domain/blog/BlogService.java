package org.springframework.site.domain.blog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.blog.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BlogService {

	private PostFormAdapter postFormAdapter;
	private PostRepository repository;
	private SearchService searchService;
	private DateService dateService;
	private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

	private static final Log logger = LogFactory.getLog(BlogService.class);

	@Autowired
	public BlogService(PostRepository repository, PostFormAdapter postFormAdapter,
					   DateService dateService,
					   SearchService searchService) {
		this.repository = repository;
		this.postFormAdapter = postFormAdapter;
		this.dateService = dateService;
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

	public Post addPost(PostForm postForm, String username) {
		Post post = postFormAdapter.createPostFromPostForm(postForm, username);
		this.repository.save(post);
		saveToIndex(post);
		return post;
	}

	public void updatePost(Post post, PostForm postForm) {
		postFormAdapter.updatePostFromPostForm(post, postForm);
		this.repository.save(post);
		saveToIndex(post);
	}

	public void deletePost(Post post) {
		this.repository.delete(post);
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
			postFormAdapter.summarize(post);
			this.repository.save(post);
		}
	}

}
