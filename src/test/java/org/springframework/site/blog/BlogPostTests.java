package org.springframework.site.blog;

import org.junit.Test;
import org.springframework.site.blog.Post;
import static org.junit.Assert.assertEquals;

public class BlogPostTests {

	@Test
	public void slugReplacesSpacesWithDashes() {
		assertEquals("this-is-a-title", new Post("This is a title", "").getSlug());
	}

	@Test
	public void slugReplacesMultipleSpacesWithASingleDash() {
		assertEquals("this-is-a-title", new Post("This    is a title", "").getSlug());
	}

	@Test
	public void slugStripsNonAlphanumericCharacters() {
		assertEquals("title-1-with-characters", new Post("Title 1, with characters';:\\|", "").getSlug());
	}

	@Test
	public void slugStripsNewLineCharacters() {
		assertEquals("title-1-on-multiple-lines", new Post("Title 1\n on multiple\nlines", "").getSlug());
	}
}
