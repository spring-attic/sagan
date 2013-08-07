package org.springframework.site.domain.blog;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.search.SearchEntry;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PostSearchEntryMapperTests {

	Post post;

	@Before
	public void setUp() throws Exception {
		post  = new Post("Blog Title", "Some Content", PostCategory.ENGINEERING);
		post.setRenderedContent("Some Rendered Content");
		post.setRenderedSummary("A Rendered Summary");
	}

	@Test
	public void testSubtitle() throws Exception {
		PostSearchEntryMapper mapper = new PostSearchEntryMapper();
		SearchEntry searchEntry = mapper.map(post);
		assertThat(searchEntry.getSubTitle(), equalTo("Blog Post"));
	}

}
