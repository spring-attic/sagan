package sagan.site.blog.support;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.blog.BlogService;
import sagan.site.blog.Post;
import sagan.site.blog.PostBuilder;
import sagan.site.blog.PostCategory;
import sagan.site.blog.PostForm;
import sagan.site.blog.PostFormat;
import sagan.site.team.MemberProfile;
import sagan.site.support.DateFactory;
import sagan.site.support.nav.PageableFactory;
import sagan.site.team.support.TeamService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BlogAdminControllerTests {
    private static final Post TEST_POST = PostBuilder.post()
            .id(123L)
            .publishYesterday()
            .build();

    private BlogAdminController controller;

    @Mock
    private BlogService blogService;

    private DateFactory dateFactory = new DateFactory();
    private ExtendedModelMap model = new ExtendedModelMap();
    private Principal principal;
    private MapBindingResult bindingResult;

    @Mock
    private TeamService teamService;

    @BeforeEach
    public void setup() {
        bindingResult = new MapBindingResult(new HashMap<>(), "postForm");
        principal = () -> "12345";

        controller = new BlogAdminController(blogService, teamService, dateFactory);
    }

    @Test
    public void dashboardShowsUsersPosts() {
        controller = new BlogAdminController(blogService, teamService, dateFactory);

        Page<Post> drafts = new PageImpl<>(
                Arrays.asList(new Post("draft post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN)),
                PageableFactory.forDashboard(1), 1);
        Page<Post> scheduled = new PageImpl<>(
                Arrays.asList(new Post("scheduled post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN)),
                PageableFactory.forDashboard(1), 1);
        Page<Post> published = new PageImpl<>(
                Arrays.asList(new Post("published post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN)),
                PageableFactory.forDashboard(1), 1);

        given(blogService.getPublishedPosts(anyObject())).willReturn(published);
        given(blogService.getDraftPosts(anyObject())).willReturn(drafts);
        given(blogService.getScheduledPosts(anyObject())).willReturn(scheduled);

        ExtendedModelMap model = new ExtendedModelMap();
        controller.dashboard(model, 1);

        assertThat(((Page<PostView>) model.get("drafts")).getContent().get(0).getTitle()).isEqualTo("draft post");
        assertThat(((Page<PostView>) model.get("scheduled")).getContent().get(0).getTitle()).isEqualTo("scheduled post");
        assertThat(((Page<PostView>) model.get("posts")).getContent().get(0).getTitle()).isEqualTo("published post");
    }

    @Test
    public void showPostModel() {
        Post post = PostBuilder.post().build();
        given(blogService.getPost(post.getId())).willReturn(post);
        controller.showPost(post.getId(), "1-post-title", model);
        PostView view = (PostView) model.get("post");
        assertThat(view).isNotNull();
    }

    @Test
    public void showPostView() {
        assertThat(controller.showPost(1L, "not important", model)).isEqualTo("blog/show");
    }

    @Test
    public void creatingABlogPostRecordsTheUser() {
        String username = "username";

        MemberProfile member = new MemberProfile();
        member.setUsername(username);

        given(teamService.fetchMemberProfile(12345L)).willReturn(Optional.of(member));
        PostForm postForm = new PostForm();

        given(blogService.addPost(eq(postForm), any())).willReturn(TEST_POST);
        controller.createPost(principal, postForm, new BindException(postForm, "postForm"), null);
        verify(blogService).addPost(postForm, member);
    }

    @Test
    public void redirectToEditPostAfterCreation() throws Exception {
        String username = "username";

        MemberProfile member = new MemberProfile();
        member.setUsername(username);

        given(teamService.fetchMemberProfile(12345L)).willReturn(Optional.of(member));

        PostForm postForm = new PostForm();
        postForm.setTitle("title");
        postForm.setContent("content");
        postForm.setCategory(PostCategory.ENGINEERING);
        Post post = PostBuilder.post().id(123L).publishAt("2013-05-06 00:00").title("Post Title").build();
        given(blogService.addPost(postForm, member)).willReturn(post);
        String result = controller.createPost(principal, postForm, bindingResult, null);

        assertThat(result).isEqualTo("redirect:/blog/2013/05/06/post-title/edit");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void attemptingToCreateADuplicatePostReturnsToEditForm() throws Exception {
        String username = "username";

        MemberProfile member = new MemberProfile();
        member.setUsername(username);

        given(teamService.fetchMemberProfile(12345L)).willReturn(Optional.of(member));

        PostForm postForm = new PostForm();
        postForm.setTitle("title");
        postForm.setContent("content");
        postForm.setCategory(PostCategory.ENGINEERING);
        Post post = PostBuilder.post().id(123L).publishAt("2013-05-06 00:00").title("Post Title").build();

        given(blogService.addPost(postForm, member)).willReturn(post);
        String result1 = controller.createPost(principal, postForm, bindingResult, null);
        assertThat(result1).isEqualTo("redirect:/blog/2013/05/06/post-title/edit");

        given(blogService.addPost(postForm, member)).willThrow(DataIntegrityViolationException.class);
        String result2 = controller.createPost(principal, postForm, bindingResult, new ExtendedModelMap());
        assertThat(result2).isEqualTo("admin/blog/new");
    }

    @Test
    public void reRenderPosts() throws Exception {
        int page = 0;
        int pageSize = 20;
        Page<Post> posts = new PageImpl<>(
                Arrays.asList(new Post("published post", "body", PostCategory.ENGINEERING, PostFormat.MARKDOWN),
                        new Post("another published post", "other body", PostCategory.NEWS_AND_EVENTS, PostFormat.MARKDOWN)),
                PageRequest.of(page, pageSize, Sort.Direction.DESC, "id"), 2);
        given(blogService.refreshPosts(page, pageSize)).willReturn(posts);
        String result = controller.refreshBlogPosts(page, pageSize);
        assertThat(result).isEqualTo("{page: 0, pageSize: 20, totalPages: 1, totalElements: 2}");
        verify(blogService, times(1)).refreshPosts(page, pageSize);
    }

}
