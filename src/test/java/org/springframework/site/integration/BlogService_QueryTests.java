package org.springframework.site.integration;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.data.domain.Pageable;
import org.springframework.site.blog.*;
import org.springframework.site.blog.web.BlogPostsPageRequest;
import org.springframework.site.blog.web.NoSuchBlogPostException;
import org.springframework.site.blog.web.ResultList;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.site.services.MarkdownService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

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

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Before
	public void setup() {
		service = new BlogService(postRepository, markdownService);
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
	public void nonExistentPostThrowsException() {
		expected.expect(NoSuchBlogPostException.class);
		service.getPost(999L);
	}

	@Test
	public void listPostsOnlyShowsPublishedPosts() {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().draft().build());

		ResultList<Post> publishedPosts = service.getPublishedPosts(firstTenPosts);
		assertThat(publishedPosts.getItems(), contains(post));
	}

	@Test
	public void listPostsForCategory() {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().category(PostCategory.ENGINEERING).build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().category(PostCategory.NEWS_AND_EVENTS).build());

		ResultList<Post> publishedPosts = service.getPublishedPosts(PostCategory.ENGINEERING, firstTenPosts);
		assertThat(publishedPosts.getItems(), contains(post));
	}

	@Test
	public void listBroadcasts() {
		Pageable firstTenPosts = new BlogPostsPageRequest(0);
		Post post = PostBuilder.post().isBroadcast().build();
		postRepository.save(post);
		postRepository.save(PostBuilder.post().build());

		ResultList<Post> publishedBroadcastPosts = service.getPublishedBroadcastPosts(firstTenPosts);
		assertThat(publishedBroadcastPosts.getItems(), contains(post));
	}
}
