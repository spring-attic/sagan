package org.springframework.site.blog;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class PostTests {

	@Test
	public void slugReplacesSpacesWithDashes() {
		assertEquals("this-is-a-title", new Post("This is a title", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugReplacesMultipleSpacesWithASingleDash() {
		assertEquals("this-is-a-title", new Post("This    is a title", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugStripsNonAlphanumericCharacters() {
		assertEquals("title-1-with-characters", new Post("Title 1, with characters';:\\|", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void slugStripsNewLineCharacters() {
		assertEquals("title-1-on-multiple-lines", new Post("Title 1\n on multiple\nlines", "", PostCategory.ENGINEERING).getSlug());
	}

	@Test
	public void pathToFullContent(){
		Post post = spy(new Post("An awesome blog post", "Awesomeness is awesome!", PostCategory.NEWS_AND_EVENTS));

		when(post.getId()).thenReturn(123L);

		assertThat(post.getPath(), is("/blog/123-an-awesome-blog-post"));
	}

}
