package sagan.blog.service;

import sagan.blog.Post;
import sagan.blog.PostCategory;
import sagan.team.MemberProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CachedBlogService {

    private BlogService blogService;

    // Required for @Cacheable proxy
    protected CachedBlogService() {
    }

    @Autowired
    public CachedBlogService(BlogService blogService) {
        this.blogService = blogService;
    }

    @Cacheable("cache.database")
    public Post getPublishedPost(String publicSlug) {
        return blogService.getPublishedPost(publicSlug);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedPosts(Pageable pageRequest) {
        return blogService.getPublishedPosts(pageRequest);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedPosts(PostCategory category, Pageable pageRequest) {
        return blogService.getPublishedPosts(category, pageRequest);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedBroadcastPosts(Pageable pageRequest) {
        return blogService.getPublishedBroadcastPosts(pageRequest);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedPostsByDate(int year, int month, int day, Pageable pageRequest) {
        return blogService.getPublishedPostsByDate(year, month, day, pageRequest);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedPostsByDate(int year, int month, Pageable pageRequest) {
        return blogService.getPublishedPostsByDate(year, month, pageRequest);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedPostsByDate(int year, Pageable pageRequest) {
        return blogService.getPublishedPostsByDate(year, pageRequest);
    }

    @Cacheable("cache.database")
    public Page<Post> getPublishedPostsForMember(MemberProfile profile, Pageable pageable) {
        return blogService.getPublishedPostsForMember(profile, pageable);
    }
}
