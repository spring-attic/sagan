package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.search.SearchEntry;
import sagan.search.support.SearchService;
import sagan.support.DateFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_UpdatePostTests {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private BlogService service;
    private Post post;
    private LocalDateTime publishAt = LocalDateTime.parse("2013-07-01 12:00", DATE_TIME_FORMATTER);
    private LocalDateTime now = LocalDateTime.parse("2013-07-01 13:00", DATE_TIME_FORMATTER);

    @Mock
    private PostRepository postRepository;

    @Mock
    private DateFactory dateFactory;

    @Mock
    private SearchService searchService;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private PostForm postForm;

    @Before
    public void setup() {
        given(dateFactory.now()).willReturn(now);

        service = new BlogService(postRepository, postFormAdapter, dateFactory, searchService);

        post = PostBuilder.post().id(123L).publishAt(publishAt).build();

        postForm = new PostForm(post);
        service.updatePost(post, postForm);
    }

    @Test
    public void postIsUpdated() {
        verify(postFormAdapter).updatePostFromPostForm(post, postForm);
    }

    @Test
    public void postIsPersisted() {
        verify(postRepository).save(post);
    }

    @Test
    public void updatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
        verify(searchService).saveToIndex((SearchEntry) anyObject());
    }

    @Test
    public void updatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
        reset(searchService);
        long postId = 123L;
        Post post = PostBuilder.post().id(postId).draft().build();
        service.updatePost(post, new PostForm(post));
        verifyZeroInteractions(searchService);
    }

}
