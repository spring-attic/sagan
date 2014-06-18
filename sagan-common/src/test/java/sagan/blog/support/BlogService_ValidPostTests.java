package sagan.blog.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.search.SearchEntry;
import sagan.search.SearchException;
import sagan.search.support.SearchService;
import sagan.support.DateTimeTestUtils;
import sagan.support.time.DateTimeFactory;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.test.util.ReflectionTestUtils;
import sagan.support.time.DateTimeUtils;

import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.anyObject;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {

    private static final String AUTHOR_USERNAME = "username";
    private Post post;
    private PostForm postForm = new PostForm();
    private LocalDateTime publishAt = DateTimeUtils.parseDateTimeNoSeconds("2013-07-01 12:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFormAdapter postFormAdapter;

    private DateTimeFactory dateTimeFactory = DateTimeTestUtils.createFixedTimeFactory("2013-07-01 13:00");

    @Mock
    private SearchService searchService;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private BlogService service;

    @Before
    public void setup() {
        given(postRepository.save((Post) anyObject())).will(invocation -> {
            Post post = (Post) invocation.getArguments()[0];
            ReflectionTestUtils.setField(post, "id", 123L);
            return post;
        });

        post = PostBuilder.post().publishAt(publishAt).build();
        given(postFormAdapter.createPostFromPostForm(postForm, AUTHOR_USERNAME)).willReturn(post);

        service = new BlogService(postRepository, postFormAdapter, dateTimeFactory, searchService);
        service.addPost(postForm, AUTHOR_USERNAME);
    }

    @Test
    public void createsAPost() {
        verify(postFormAdapter).createPostFromPostForm(postForm, AUTHOR_USERNAME);
    }

    @Test
    public void postIsPersisted() {
        verify(postRepository).save((Post) anyObject());
    }

    @Test
    public void creatingABlogPost_addsThatPostToTheSearchIndexIfPublished() {
        verify(searchService).saveToIndex((SearchEntry) anyObject());
    }

    @Test
    public void blogIsSavedWhenSearchServiceIsDown() {
        reset(postRepository);
        willThrow(SearchException.class).given(searchService).saveToIndex((SearchEntry) anyObject());
        post = service.addPost(postForm, AUTHOR_USERNAME);
        verify(postRepository).save(post);
    }

    @Test
    public void creatingABlogPost_doesNotSaveToSearchIndexIfNotLive() throws Exception {
        reset(searchService);

        PostForm draftPostForm = new PostForm();
        draftPostForm.setDraft(true);

        Post draft = PostBuilder.post().draft().build();
        given(postFormAdapter.createPostFromPostForm(draftPostForm, AUTHOR_USERNAME)).willReturn(draft);

        service.addPost(draftPostForm, AUTHOR_USERNAME);
        verifyZeroInteractions(searchService);
    }

}
