package integration.blog;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.site.domain.blog.BlogService;
import org.springframework.site.domain.blog.Post;
import org.springframework.site.domain.blog.PostBuilder;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.site.domain.blog.PostFormAdapter;
import org.springframework.site.domain.blog.PostRepository;
import org.springframework.site.domain.services.DateService;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.search.SearchService;
import org.springframework.site.web.PageableFactory;
import org.springframework.site.web.blog.EntityNotFoundException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

	@Before
	public void setup() {
		initMocks(this);
		given(dateService.now()).willReturn(new Date());

		service = new BlogService(postRepository, postFormAdapter, dateService,searchService);
		assertThat(postRepository.findAll().size(), equalTo(0));
	}

	@Test
	public void postIsRetrievableById() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		assertThat(service.getPost(post.getId()), equalTo(post));
	}

	@Test
	public void postIsRetrievableByTitleAndCreatedDate() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		assertThat(service.getPost(post.getTitle(), post.getCreatedAt()), equalTo(post));
	}

	@Test
	public void publishedPostIsRetrievable() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		assertThat(service.getPublishedPost(post.getPublicSlug()), equalTo(post));
	}

	@Test
	public void getPublishedDoesNotFindDrafts() {
		Post post = PostBuilder.post().draft().build();
		postRepository.save(post);

		expected.expect(EntityNotFoundException.class);
		service.getPublishedPost(post.getPublicSlug());
	}

	@Test
	public void getPublishedDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-15 00:00").build();
		postRepository.save(post);

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		given(dateService.now()).willReturn(today);

		expected.expect(EntityNotFoundException.class);
		service.getPublishedPost(post.getPublicSlug());
	}

	@Test
	public void nonExistentPostThrowsException() {
		expected.expect(EntityNotFoundException.class);
		service.getPost(999L);
	}

	@Test
	public void getPublishedPostsOnlyShowsPublishedPosts() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().draft().build());

		Page<Post> publishedPosts = service.getPublishedPosts(FIRST_TEN_POSTS);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void getPublishedPostsDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-13 00:00").build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-15 00:00").build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		given(dateService.now()).willReturn(today);

		assertThat(service.getPublishedPosts(FIRST_TEN_POSTS), contains(post));
	}

	@Test
	public void getScheduledPostsOnlyShowsPublishedPosts() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-15 00:00").build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().draft().publishAt("2013-06-15 00:00").build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		given(dateService.now()).willReturn(today);

		assertThat(service.getScheduledPosts(FIRST_TEN_POSTS), contains(post));
	}

	@Test
	public void getScheduledPostsDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-15 00:00").build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-13 00:00").build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		given(dateService.now()).willReturn(today);

		assertThat(service.getScheduledPosts(FIRST_TEN_POSTS), contains(post));
	}

	@Test
	public void listPostsForCategory() {
		Post post = PostBuilder.post().category(PostCategory.ENGINEERING).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().category(PostCategory.NEWS_AND_EVENTS).build());

		Page<Post> publishedPosts = service.getPublishedPosts(PostCategory.ENGINEERING, FIRST_TEN_POSTS);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void listPostsForCategoryDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-13 00:00").category(PostCategory.ENGINEERING).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-15 00:00").category(PostCategory.ENGINEERING).build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		given(dateService.now()).willReturn(today);

		Page<Post> publishedPosts = service.getPublishedPosts(PostCategory.ENGINEERING, FIRST_TEN_POSTS);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void paginationInfoBasedOnCurrentPageAndTotalPosts() {
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
		Post post = PostBuilder.post().isBroadcast().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().build());

		Page<Post> publishedBroadcastPosts = service.getPublishedBroadcastPosts(FIRST_TEN_POSTS);
		assertThat(publishedBroadcastPosts.getContent(), contains(post));
	}

	@Test
	public void listBroadcastsDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-13 00:00").isBroadcast().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-15 00:00").isBroadcast().build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		given(dateService.now()).willReturn(today);

		Page<Post> publishedBroadcastPosts = service.getPublishedBroadcastPosts(FIRST_TEN_POSTS);
		assertThat(publishedBroadcastPosts.getContent(), contains(post));
	}

	@Test
	public void allPosts() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		Post draft = PostBuilder.post().draft().build();
		postRepository.save(draft);

		Page<Post> allPosts = service.getAllPosts(FIRST_TEN_POSTS);
		assertThat(allPosts.getContent(), containsInAnyOrder(post, draft));
	}

	@Test
	public void getPublishedPostsForAuthorOnlyShowsAuthorsPosts() {
		MemberProfile profile = new MemberProfile();
		profile.setUsername("myauthor");

		Post post = PostBuilder.post().author(profile).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().build());

		Page<Post> publishedPosts = service.getPublishedPostsForMember(profile, FIRST_TEN_POSTS);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void getPublishedPostsForAuthorOnlyShowsPublishedPosts() {
		MemberProfile profile = new MemberProfile();
		profile.setUsername("myauthor");

		Post post = PostBuilder.post().author(profile).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().author(profile).draft().build());

		Page<Post> publishedPosts = service.getPublishedPostsForMember(profile, FIRST_TEN_POSTS);
		assertThat(publishedPosts.getContent(), contains(post));
	}
}
