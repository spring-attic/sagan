package org.springframework.site.integration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.site.blog.*;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.site.blog.web.NoSuchBlogPostException;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.services.DateService;
import org.springframework.site.services.MarkdownService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ApplicationConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@TransactionConfiguration(defaultRollback=true)
@Transactional
public class BlogService_QueryTests {

	private BlogService service;

	@Autowired
	private PostRepository postRepository;

	@Mock
	private MarkdownService markdownService;

	@Mock
	private DateService dateService;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Autowired LocalContainerEntityManagerFactoryBean fb;

	@Before
	public void setup() {
		initMocks(this);
		when(dateService.now()).thenReturn(new Date());

		service = new BlogService(postRepository, markdownService, dateService);
		assertThat(postRepository.findAll().size(), equalTo(0));
	}

	@Test
	public void postIsRetrievable() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		assertThat(service.getPost(post.getId()), equalTo(post));
	}

	@Test
	public void publishedPostIsRetrievable() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		assertThat(service.getPublishedPost(post.getId()), equalTo(post));
	}

	@Test
	public void getPublishedDoesNotFindDrafts() {
		Post post = PostBuilder.post().draft().build();
		postRepository.save(post);

		expected.expect(NoSuchBlogPostException.class);
		service.getPublishedPost(post.getId());
	}

	@Test
	public void getPublishedDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-15 00:00").build();
		postRepository.save(post);

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		when(dateService.now()).thenReturn(today);

		expected.expect(NoSuchBlogPostException.class);
		service.getPublishedPost(post.getId());
	}

	@Test
	public void nonExistentPostThrowsException() {
		expected.expect(NoSuchBlogPostException.class);
		service.getPost(999L);
	}

	@Test
	public void getPublishedPostsOnlyShowsPublishedPosts() {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().draft().build());

		Page<Post> publishedPosts = service.getPublishedPosts(firstTenPosts);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void getPublishedPostsDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-13 00:00").build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-15 00:00").build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		when(dateService.now()).thenReturn(today);

		assertThat(service.getPublishedPosts(new BlogPostsPageRequest(0)), contains(post));
	}

	@Test
	public void getScheduledPostsOnlyShowsPublishedPosts() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-15 00:00").build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().draft().publishAt("2013-06-15 00:00").build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		when(dateService.now()).thenReturn(today);

		assertThat(service.getScheduledPosts(new BlogPostsPageRequest(0)), contains(post));
	}

	@Test
	public void getScheduledPostsDoesNotFindFutureSchedulePost() throws ParseException {
		Post post = PostBuilder.post().publishAt("2013-06-15 00:00").build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-13 00:00").build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		when(dateService.now()).thenReturn(today);

		assertThat(service.getScheduledPosts(new BlogPostsPageRequest(0)), contains(post));
	}

	@Test
	public void listPostsForCategory() {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().category(PostCategory.ENGINEERING).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().category(PostCategory.NEWS_AND_EVENTS).build());

		Page<Post> publishedPosts = service.getPublishedPosts(PostCategory.ENGINEERING, firstTenPosts);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void listPostsForCategoryDoesNotFindFutureSchedulePost() throws ParseException {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().publishAt("2013-06-13 00:00").category(PostCategory.ENGINEERING).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-15 00:00").category(PostCategory.ENGINEERING).build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		when(dateService.now()).thenReturn(today);

		Page<Post> publishedPosts = service.getPublishedPosts(PostCategory.ENGINEERING, firstTenPosts);
		assertThat(publishedPosts.getContent(), contains(post));
	}

	@Test
	public void paginationInfoBasedOnCurrentPageAndTotalPosts() {
		List<Post> posts = new ArrayList<Post>();
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
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().isBroadcast().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().build());

		Page<Post> publishedBroadcastPosts = service.getPublishedBroadcastPosts(firstTenPosts);
		assertThat(publishedBroadcastPosts.getContent(), contains(post));
	}

	@Test
	public void listBroadcastsDoesNotFindFutureSchedulePost() throws ParseException {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().publishAt("2013-06-13 00:00").isBroadcast().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().publishAt("2013-06-15 00:00").isBroadcast().build());

		Date today = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2013-06-14 00:00");
		when(dateService.now()).thenReturn(today);

		Page<Post> publishedBroadcastPosts = service.getPublishedBroadcastPosts(firstTenPosts);
		assertThat(publishedBroadcastPosts.getContent(), contains(post));
	}

	@Test
	public void allPosts() {
		Post post = PostBuilder.post().build();
		postRepository.save(post);

		Post draft = PostBuilder.post().draft().build();
		postRepository.save(draft);

		Page<Post> allPosts = service.getAllPosts(new BlogPostsPageRequest(0));
		assertThat(allPosts.getContent(), containsInAnyOrder(post, draft));
	}
}
