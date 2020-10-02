package sagan.site.blog;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.support.DateFactory;
import sagan.support.DateTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BlogService_UpdatePostTests {

    private BlogService service;
    private Post post;
    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");
    private Date now = DateTestUtils.getDate("2013-07-01 13:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private DateFactory dateFactory;

    @Mock
    private PostFormAdapter postFormAdapter;

    private PostForm postForm;

    @BeforeEach
    public void setup() {
        service = new BlogService(postRepository, postFormAdapter, dateFactory);

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

}
