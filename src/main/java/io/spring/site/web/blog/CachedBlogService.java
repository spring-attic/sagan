package io.spring.site.web.blog;

import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CachedBlogService {

    private BlogService blogService;

    //Required for @Cacheable proxy
    protected CachedBlogService() {}

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
}
