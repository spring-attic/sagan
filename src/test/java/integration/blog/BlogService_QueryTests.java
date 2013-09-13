package integration.blog;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.blog.Post;
import io.spring.site.domain.blog.PostBuilder;
import io.spring.site.domain.blog.PostFormAdapter;
import io.spring.site.domain.blog.PostRepository;
import io.spring.site.domain.services.DateService;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.search.SearchService;
import io.spring.site.web.PageableFactory;
import io.spring.site.web.blog.EntityNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.spring.site.domain.blog.PostCategory.ENGINEERING;
import static io.spring.site.domain.blog.PostCategory.NEWS_AND_EVENTS;
import static io.spring.site.domain.blog.PostCategory.RELEASES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;

public class BlogService_QueryTests extends IntegrationTestBase {

    private static final Pageable FIRST_TEN_POSTS = PageableFactory.forLists(1);

    @Mock
    private DateService dateService;

    @Mock
    private SearchService searchService;

    @Mock
    private PostFormAdapter postFormAdapter;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Autowired
    private PostRepository postRepository;

    private BlogService service;

    private Date yesterday;
    private Date today;
    private Date tomorrow;

    private Post published;
    private Post scheduled;
    private Post draft;


    @Before
    public void setup() throws Exception{
        initMocks(this);
        service = new BlogService(postRepository, postFormAdapter, dateService,searchService);
        setupDates();
        setupFixtureData();
    }

    private void setupDates() throws Exception{
        yesterday = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-13");
        today = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-14");
        tomorrow = new SimpleDateFormat("yyyy-MM-dd").parse("2013-06-15");

        given(dateService.now()).willReturn(today);
    }

    private void setupFixtureData() throws Exception{
        assertThat(postRepository.findAll().size(), equalTo(0));

        scheduled = PostBuilder.post().category(ENGINEERING).publishAt(tomorrow).build();
        postRepository.save(scheduled);

        published = PostBuilder.post().category(ENGINEERING).publishAt(yesterday).build();
        postRepository.save(published);

        draft = PostBuilder.post().draft().build();
        postRepository.save(draft);
    }

    @Test
    public void postIsRetrievableById() {
        assertThat(service.getPost(published.getId()), equalTo(published));
    }

    @Test
    public void postIsRetrievableByTitleAndCreatedDate() {
        assertThat(service.getPost(published.getTitle(), published.getCreatedAt()), equalTo(published));
    }

    @Test
    public void publishedPostIsRetrievable() {
        assertThat(service.getPublishedPost(published.getPublicSlug()), equalTo(published));
    }

    @Test
    public void getPublishedDoesNotFindDrafts() {
        Post draft = PostBuilder.post().draft().build();
        postRepository.save(draft);
        expected.expect(EntityNotFoundException.class);
        service.getPublishedPost(draft.getPublicSlug());
    }

    @Test
    public void getPublishedDoesNotFindScheduledPost() throws ParseException {
        expected.expect(EntityNotFoundException.class);
        service.getPublishedPost(scheduled.getPublicSlug());
    }

    @Test
    public void nonExistentPostThrowsException() {
        expected.expect(EntityNotFoundException.class);
        service.getPost(999L);
    }

    @Test
    public void getPublishedPostsWithPagination() {
        Page<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS);

        assertThat(publishedPosts.getContent(), contains(published));
    }

    @Test
    public void getAllPublishedPosts() {
        Post anotherPost = PostBuilder.post().publishAt(yesterday).title("another post").build();
        postRepository.save(anotherPost);
        List<Post> publishedPosts = service.getAllPublishedPosts();

        assertThat(publishedPosts, containsInAnyOrder(published, anotherPost));
    }

    @Test
    public void getPublishedPostsExcludesDrafts() throws ParseException {
        List<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS).getContent();

        assertThat(draft, not(isIn(publishedPosts)));
    }

    @Test
    public void getPublishedPostsExcludesScheduledPosts() throws ParseException {
        List<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduled, not(isIn(publishedPosts)));
    }

    @Test
    public void getScheduledPosts() throws ParseException {
        List<Post> scheduledPosts = service.getScheduledPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduledPosts, contains(scheduled));
    }

    @Test
    public void getScheduledPostsExcludesScheduledDrafts() throws ParseException {
        Post scheduledDraft = PostBuilder.post().draft().publishAt(tomorrow).build();
        postRepository.save(scheduledDraft);

        List<Post> scheduledPosts = service.getScheduledPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduledDraft, not(isIn(scheduledPosts)));
    }

    @Test
    public void getScheduledPostsExcludesPublishedPosts() throws ParseException {
        List<Post> scheduledPosts = service.getScheduledPosts(FIRST_TEN_POSTS).getContent();

        assertThat(published, not(isIn(scheduledPosts)));
    }

    @Test
    public void listPostsForCategory() {
        Post newsPost = PostBuilder.post().publishAt(yesterday).category(NEWS_AND_EVENTS).build();
        postRepository.save(newsPost);

        Page<Post> publishedPosts = service.getPublishedPosts(NEWS_AND_EVENTS, FIRST_TEN_POSTS);

        assertThat(publishedPosts.getContent(), contains(newsPost));
    }

    @Test
    public void listPostsForCategoryExcludesScheduledPosts() throws ParseException {
        Post publishedRelease = PostBuilder.post().publishAt(yesterday).category(RELEASES).build();
        postRepository.save(publishedRelease);
        Post scheduledRelease = PostBuilder.post().publishAt(tomorrow).category(RELEASES).build();
        postRepository.save(scheduledRelease);

        List<Post> releases = service.getPublishedPosts(RELEASES, FIRST_TEN_POSTS).getContent();

        assertThat(scheduledRelease, not(isIn(releases)));
    }

    @Test
    public void paginationInfoBasedOnCurrentPageAndTotalPosts() {
        postRepository.deleteAll();

        List<Post> posts = new ArrayList<>();
        long itemCount = 11;
        for (int i = 0; i < itemCount; ++i) {
            posts.add(PostBuilder.post().build());
        }
        postRepository.save(posts);

        PageRequest pageRequest = new PageRequest(0, 10);
        Page<Post> result = service.getAllPosts(pageRequest);
        assertThat(result.getTotalElements(), is(itemCount));
    }

    @Test
    public void listBroadcasts() {
        Post broadcast = PostBuilder.post().isBroadcast().publishAt(yesterday).build();
        postRepository.save(broadcast);

        List<Post> broadcasts = service.getPublishedBroadcastPosts(FIRST_TEN_POSTS).getContent();

        assertThat(broadcasts, contains(broadcast));
    }

    @Test
    public void listBroadcastsExcludesScheduledPosts() throws ParseException {
        Post publishedBroadcast = PostBuilder.post().isBroadcast().publishAt(yesterday).build();
        postRepository.save(publishedBroadcast);
        Post scheduledBroadcast = PostBuilder.post().isBroadcast().publishAt(tomorrow).build();
        postRepository.save(scheduledBroadcast);

        List<Post> broadcasts = service.getPublishedBroadcastPosts(FIRST_TEN_POSTS).getContent();

        assertThat(scheduledBroadcast, not(isIn(broadcasts)));
    }

    @Test
    public void allPosts() {
        Page<Post> allPosts = service.getAllPosts(FIRST_TEN_POSTS);

        assertThat(allPosts.getContent(), containsInAnyOrder(published, scheduled, draft));
    }

    @Test
    public void getPublishedPostsForAuthorOnlyShowsAuthorsPosts() {
        MemberProfile profile = new MemberProfile();
        profile.setUsername("myauthor");

        Post post = PostBuilder.post().author(profile).publishAt(yesterday).build();

        postRepository.save(post);
        postRepository.save(PostBuilder.post().build());

        Page<Post> publishedPosts = service.getPublishedPostsForMember(profile, FIRST_TEN_POSTS);
        assertThat(publishedPosts.getContent(), contains(post));
    }

    @Test
    public void getPublishedPostsForAuthorOnlyShowsPublishedPosts() {
        MemberProfile profile = new MemberProfile();
        profile.setUsername("myauthor");

        Post post = PostBuilder.post().author(profile).publishAt(yesterday).build();

        postRepository.save(post);
        postRepository.save(PostBuilder.post().author(profile).draft().build());

        Page<Post> publishedPosts = service.getPublishedPostsForMember(profile, FIRST_TEN_POSTS);
        assertThat(publishedPosts.getContent(), contains(post));
    }
}
