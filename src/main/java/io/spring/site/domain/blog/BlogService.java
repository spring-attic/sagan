package io.spring.site.domain.blog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.spring.site.domain.services.DateService;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.search.SearchService;
import io.spring.site.web.blog.EntityNotFoundException;

import java.util.Date;
import java.util.List;

@Service
public class BlogService {

    private PostFormAdapter postFormAdapter;
    private PostRepository postRepository;
    private SearchService searchService;
    private DateService dateService;
    private PostSearchEntryMapper mapper = new PostSearchEntryMapper();

    private static final Log logger = LogFactory.getLog(BlogService.class);

    @Autowired
    public BlogService(PostRepository postRepository, PostFormAdapter postFormAdapter,
                       DateService dateService,
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
            throw new EntityNotFoundException("Blog post not found with Id=" + postId);
        }
        return post;
    }

    public Post getPost(String title, Date createdAt) {
        return postRepository.findByTitleAndCreatedAt(title, createdAt);
    }

    public Post getPublishedPost(String publicSlug) {
        Post post = this.postRepository.findByPublicSlugAndDraftFalseAndPublishAtBefore(publicSlug,
                this.dateService.now());
        if (post == null) {
            throw new EntityNotFoundException("Blog post not found with Id=" + publicSlug);
        }
        return post;
    }

    public Page<Post> getDraftPosts(Pageable pageRequest) {
        return this.postRepository.findByDraftTrue(pageRequest);
    }

    public Page<Post> getPublishedPosts(Pageable pageRequest) {
        return this.postRepository.findByDraftFalseAndPublishAtBefore(this.dateService.now(),
                pageRequest);
    }

    public Page<Post> getPublishedPostsByDate(int year, int month, int day, Pageable pageRequest) {
        return this.postRepository.findByDate(year, month, day, pageRequest);
    }

    public Page<Post> getPublishedPostsByDate(int year, int month, Pageable pageRequest) {
        return this.postRepository.findByDate(year, month, pageRequest);
    }

    public Page<Post> getPublishedPostsByDate(int year, Pageable pageRequest) {
        return this.postRepository.findByDate(year, pageRequest);
    }

    public Page<Post> getScheduledPosts(Pageable pageRequest) {
        return this.postRepository.findByDraftFalseAndPublishAtAfter(this.dateService.now(),
                pageRequest);
    }

    public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
        return this.postRepository.findByCategoryAndDraftFalseAndPublishAtBefore(category,
                this.dateService.now(), pageRequest);
    }

    public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
        return this.postRepository.findByBroadcastAndDraftFalseAndPublishAtBefore(true,
                this.dateService.now(), pageRequest);
    }

    public Page<Post> getPublishedPostsForMember(MemberProfile profile,
            Pageable pageRequest) {
        return this.postRepository.findByDraftFalseAndAuthorAndPublishAtBefore(profile,
                this.dateService.now(), pageRequest);
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

    public void reIndexAllPosts() {
        List<Post> posts = postRepository.findByDraftFalseAndPublishAtBefore(dateService.now());
        for (Post post : posts) {
            saveToIndex(post);
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
