package sagan.blog.service;

import sagan.blog.BlogPostNotFoundException;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.blog.PostForm;
import sagan.blog.service.index.PostSearchEntryMapper;
import sagan.search.service.SearchService;
import sagan.team.MemberProfile;
import sagan.util.service.DateService;
import sagan.util.service.db.DatabaseConfig;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    private PostFormAdapter postFormAdapter;
    private PostRepository postRepository;
    private SearchService searchService;
    private DateService dateService;
    private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

    private static final Log logger = LogFactory.getLog(BlogService.class);

    @Autowired
    public BlogService(PostRepository postRepository, PostFormAdapter postFormAdapter, DateService dateService,
                       SearchService searchService) {
        this.postRepository = postRepository;
        this.postFormAdapter = postFormAdapter;
        this.dateService = dateService;
        this.searchService = searchService;
    }

    // Query methods

    public Post getPost(Long postId) {
        Post post = this.postRepository.findOne(postId);
        if (post == null) {
            throw new BlogPostNotFoundException(postId);
        }
        return post;
    }

    public Post getPost(String title, Date createdAt) {
        return postRepository.findByTitleAndCreatedAt(title, createdAt);
    }

    public Page<Post> getDraftPosts(Pageable pageRequest) {
        return this.postRepository.findByDraftTrue(pageRequest);
    }

    public Page<Post> getScheduledPosts(Pageable pageRequest) {
        return this.postRepository.findByDraftFalseAndPublishAtAfter(this.dateService.now(), pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPosts(Pageable pageRequest) {
        return this.postRepository.findByDraftFalseAndPublishAtBefore(this.dateService.now(), pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Post getPublishedPost(String publicSlug) {
        Post post =
                this.postRepository.findByPublicSlugAndDraftFalseAndPublishAtBefore(publicSlug, this.dateService.now());
        if (post == null) {
            throw new BlogPostNotFoundException(publicSlug);
        }
        return post;
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public List<Post> getAllPublishedPosts() {
        return this.postRepository.findByDraftFalseAndPublishAtBefore(this.dateService.now());
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsByDate(int year, int month, int day, Pageable pageRequest) {
        return this.postRepository.findByDate(year, month, day, pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsByDate(int year, int month, Pageable pageRequest) {
        return this.postRepository.findByDate(year, month, pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsByDate(int year, Pageable pageRequest) {
        return this.postRepository.findByDate(year, pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
        return this.postRepository.findByCategoryAndDraftFalseAndPublishAtBefore(category, this.dateService.now(),
                pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
        return this.postRepository.findByBroadcastAndDraftFalseAndPublishAtBefore(true, this.dateService.now(),
                pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsForMember(MemberProfile profile, Pageable pageRequest) {
        return this.postRepository.findByDraftFalseAndAuthorAndPublishAtBefore(profile, this.dateService.now(),
                pageRequest);
    }

    public Page<Post> getAllPosts(Pageable pageRequest) {
        return this.postRepository.findAll(pageRequest);
    }

    public Post addPost(PostForm postForm, String username) {
        Post post = postFormAdapter.createPostFromPostForm(postForm, username);
        this.postRepository.save(post);
        saveToIndex(post);
        return post;
    }

    public void updatePost(Post post, PostForm postForm) {
        postFormAdapter.updatePostFromPostForm(post, postForm);
        this.postRepository.save(post);
        saveToIndex(post);
    }

    public void deletePost(Post post) {
        this.postRepository.delete(post);
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

    public void resummarizeAllPosts() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            postFormAdapter.summarize(post);
            this.postRepository.save(post);
        }
    }

}
