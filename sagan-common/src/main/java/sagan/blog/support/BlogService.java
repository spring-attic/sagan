package sagan.blog.support;

import sagan.DatabaseConfig;

import sagan.blog.BlogPostMovedException;
import sagan.blog.BlogPostNotFoundException;
import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.search.support.SearchService;
import sagan.support.DateService;
import sagan.team.MemberProfile;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service providing high-level, selectively cached data access and other {@link Post}
 * -related operations.
 *
 * @author Pivotal Labs
 * @author Chris Beams
 */
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
        Post post = postRepository.findOne(postId);
        if (post == null) {
            throw new BlogPostNotFoundException(postId);
        }
        return post;
    }

    public Post getPost(String title, Date createdAt) {
        return postRepository.findByTitleAndCreatedAt(title, createdAt);
    }

    public Page<Post> getDraftPosts(Pageable pageRequest) {
        return postRepository.findByDraftTrue(pageRequest);
    }

    public Page<Post> getScheduledPosts(Pageable pageRequest) {
        return postRepository.findByDraftFalseAndPublishAtAfter(dateService.now(), pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPosts(Pageable pageRequest) {
        return postRepository.findByDraftFalseAndPublishAtBefore(dateService.now(), pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Post getPublishedPost(String publicSlug) {
        Date now = dateService.now();
        Post post =
                postRepository.findByPublicSlugAndDraftFalseAndPublishAtBefore(publicSlug, now);
        if(post == null) {
            Set<String> publicSlugAliases = new HashSet<String>();
            publicSlugAliases.add(publicSlug);
            post = postRepository.findByPublicSlugAliasesInAndDraftFalseAndPublishAtBefore(publicSlugAliases, now);
            if(post != null) {
                throw new BlogPostMovedException(post.getPublicSlug());
            }
        }
        if (post == null) {
            throw new BlogPostNotFoundException(publicSlug);
        }
        return post;
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public List<Post> getAllPublishedPosts() {
        return postRepository.findByDraftFalseAndPublishAtBefore(dateService.now());
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsByDate(int year, int month, int day, Pageable pageRequest) {
        return postRepository.findByDate(year, month, day, pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsByDate(int year, int month, Pageable pageRequest) {
        return postRepository.findByDate(year, month, pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsByDate(int year, Pageable pageRequest) {
        return postRepository.findByDate(year, pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
        return postRepository.findByCategoryAndDraftFalseAndPublishAtBefore(category, dateService.now(),
                pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
        return postRepository.findByBroadcastAndDraftFalseAndPublishAtBefore(true, dateService.now(),
                pageRequest);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public Page<Post> getPublishedPostsForMember(MemberProfile profile, Pageable pageRequest) {
        return postRepository.findByDraftFalseAndAuthorAndPublishAtBefore(profile, dateService.now(),
                pageRequest);
    }

    public Page<Post> getAllPosts(Pageable pageRequest) {
        return postRepository.findAll(pageRequest);
    }

    public Post addPost(PostForm postForm, String username) {
        Post post = postFormAdapter.createPostFromPostForm(postForm, username);
        postRepository.save(post);
        saveToIndex(post);
        return post;
    }

    public void updatePost(Post post, PostForm postForm) {
        postFormAdapter.updatePostFromPostForm(post, postForm);
        postRepository.save(post);
        saveToIndex(post);
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    private void saveToIndex(Post post) {
        if (post.isLiveOn(dateService.now())) {
            try {
                searchService.saveToIndex(mapper.map(post));
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public void resummarizeAllPosts() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            postFormAdapter.summarize(post);
            postRepository.save(post);
        }
    }

}
