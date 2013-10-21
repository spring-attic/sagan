package sagan.blog.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.PostForm;
import sagan.util.DateTestUtils;

import sagan.util.service.DateService;
import sagan.search.SearchEntry;
import sagan.search.SearchException;
import sagan.search.service.SearchService;

import java.util.Date;

import static org.mockito.BDDMockito.anyObject;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.verifyZeroInteractions;
import static org.mockito.BDDMockito.willThrow;

@RunWith(MockitoJUnitRunner.class)
public class BlogService_ValidPostTests {

    private static final String AUTHOR_USERNAME = "username";
    private Post post;
    private PostForm postForm = new PostForm();
    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Mock
    private DateService dateService;


    @Mock
    private SearchService searchService;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    private BlogService service;

    @Before
    public void setup() {
        given(dateService.now()).willReturn(now);

        given(postRepository.save((Post) anyObject())).will(new Answer<Post>() {
            @Override
            public Post answer(InvocationOnMock invocation) throws Throwable {
                Post post = (Post) invocation.getArguments()[0];
                ReflectionTestUtils.setField(post, "id", 123L);
                return post;
            }
        });

        post = PostBuilder.post()
                .publishAt(publishAt)
                .build();
        given(postFormAdapter.createPostFromPostForm(postForm, AUTHOR_USERNAME)).willReturn(post);

        service = new BlogService(postRepository, postFormAdapter, dateService, searchService);
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
