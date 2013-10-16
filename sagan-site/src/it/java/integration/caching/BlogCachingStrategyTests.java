package integration.caching;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import utils.PostBuilder;
import io.spring.site.domain.blog.PostCategory;
import io.spring.site.web.PageableFactory;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import utils.SetSystemProperty;

import java.util.Arrays;
import java.util.List;

import static integration.caching.BlogCachingStrategyTests.TestConfiguration;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = { TestConfiguration.class })
public class BlogCachingStrategyTests extends IntegrationTestBase {

    private Page<Post> pageOfPosts;
    private Post post = PostBuilder.post().build();

    @Configuration
    public static class TestConfiguration {
        @Bean
        @Primary
        public BlogService mockBlogService() {
            return mock(BlogService.class);
        }
    }

    @ClassRule
    public static SetSystemProperty timeToLive = new SetSystemProperty("cache.database.timetolive", "1");

    @Autowired
    private BlogService blogService;


    @Before
    public void setUp() throws Exception {
        List<Post> posts = Arrays.asList(post);
        pageOfPosts = new PageImpl<>(posts, PageableFactory.forLists(1), 1);
    }

    @Test
    public void cachingABlogPost() throws Exception {
        given(blogService.getPublishedPost(anyString())).willReturn(post);
        getTwice("/blog/2012/12/25/some-post");
        verify(blogService, times(1)).getPublishedPost(anyString());
    }

    @Test
    public void cachingBlogPostList() throws Exception {
        given(blogService.getPublishedPosts(any(PageRequest.class))).willReturn(pageOfPosts);
        getTwice("/blog/");
        verify(blogService, times(1)).getPublishedPosts(any(Pageable.class));
    }

    @Test
    public void cachingBlogEngineeringPostList() throws Exception {
        given(blogService.getPublishedPosts(eq(PostCategory.ENGINEERING), any(PageRequest.class))).willReturn(pageOfPosts);
        getTwice("/blog/category/engineering");
        verify(blogService, times(1)).getPublishedPosts(eq(PostCategory.ENGINEERING), any(Pageable.class));
    }

    @Test
    public void cachingBlogNewsPostList() throws Exception {
        given(blogService.getPublishedPosts(eq(PostCategory.NEWS_AND_EVENTS), any(PageRequest.class))).willReturn(pageOfPosts);
        getTwice("/blog/category/news");
        verify(blogService, times(1)).getPublishedPosts(eq(PostCategory.NEWS_AND_EVENTS), any(Pageable.class));
    }

    @Test
    public void cachingBlogReleasePostList() throws Exception {
        given(blogService.getPublishedPosts(eq(PostCategory.RELEASES), any(PageRequest.class))).willReturn(pageOfPosts);
        getTwice("/blog/category/releases");
        verify(blogService, times(1)).getPublishedPosts(eq(PostCategory.RELEASES), any(Pageable.class));
    }

    @Test
    public void cachingBlogBroadcastPostList() throws Exception {
        given(blogService.getPublishedBroadcastPosts(any(Pageable.class))).willReturn(pageOfPosts);
        getTwice("/blog/broadcasts");
        verify(blogService, times(1)).getPublishedBroadcastPosts(any(Pageable.class));
    }

    @Test
    public void cachingBlogPostListByYear() throws Exception {
        given(blogService.getPublishedPostsByDate(anyInt(), any(Pageable.class))).willReturn(pageOfPosts);
        getTwice("/blog/2012");
        verify(blogService, times(1)).getPublishedPostsByDate(anyInt(), any(Pageable.class));
    }

    @Test
    public void cachingBlogPostListByYear_Month() throws Exception {
        given(blogService.getPublishedPostsByDate(anyInt(), anyInt(), any(Pageable.class))).willReturn(pageOfPosts);
        getTwice("/blog/2012/12");
        verify(blogService, times(1)).getPublishedPostsByDate(anyInt(), anyInt(), any(Pageable.class));
    }

    @Test
    public void cachingBlogPostListByYear_Month_Day() throws Exception {
        given(blogService.getPublishedPostsByDate(anyInt(), anyInt(), anyInt(), any(Pageable.class))).willReturn(pageOfPosts);
        getTwice("/blog/2012/12/25");
        verify(blogService, times(1)).getPublishedPostsByDate(anyInt(), anyInt(), anyInt(), any(Pageable.class));
    }

    private void getTwice(String path) throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get(path)).andExpect(status().isOk());;
        this.mockMvc.perform(MockMvcRequestBuilders.get(path)).andExpect(status().isOk());;
    }

}
