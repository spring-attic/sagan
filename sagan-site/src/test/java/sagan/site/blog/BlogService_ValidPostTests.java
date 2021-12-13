package sagan.site.blog;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.SiteProperties;
import sagan.site.support.DateFactory;
import sagan.site.support.DateTestUtils;
import sagan.site.team.MemberProfile;

import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
public class BlogService_ValidPostTests {

    private static final MemberProfile AUTHOR = new MemberProfile();
    private Post post;
    private PostForm postForm = new PostForm();
    private Date publishAt = DateTestUtils.getDate("2013-07-01 12:00");

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Mock
    private DateFactory dateFactory;

    private BlogService service;

    @BeforeEach
    public void setup() {
        given(postRepository.save(any())).will(invocation -> {
            Post post = (Post) invocation.getArguments()[0];
            ReflectionTestUtils.setField(post, "id", 123L);
            return post;
        });

        post = PostBuilder.post().publishAt(publishAt).build();
        given(postFormAdapter.createPostFromPostForm(postForm, AUTHOR)).willReturn(post);

        service = new BlogService(postRepository, postFormAdapter, dateFactory, new SiteProperties());
        service.addPost(postForm, AUTHOR);
    }

    @Test
    public void createsAPost() {
        verify(postFormAdapter).createPostFromPostForm(postForm, AUTHOR);
    }

    @Test
    public void postIsPersisted() {
        verify(postRepository).save(any());
    }

}
