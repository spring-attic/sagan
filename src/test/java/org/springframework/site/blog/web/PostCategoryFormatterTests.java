package org.springframework.site.blog.web;

import org.junit.Test;
import org.springframework.site.blog.PostCategory;
import org.springframework.site.blog.web.PostCategoryFormatter;

import java.text.ParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PostCategoryFormatterTests {
	private PostCategoryFormatter formatter = new PostCategoryFormatter();

	@Test
	public void itConvertsUrlSlugStringsToPostCategories() throws ParseException {
		assertThat(formatter.parse(PostCategory.ENGINEERING.getUrlSlug(), null), equalTo(PostCategory.ENGINEERING));
	}

	@Test
	public void itConvertsEnumNameStringsToPostCategories() throws ParseException {
		assertThat(formatter.parse(PostCategory.ENGINEERING.name(), null), equalTo(PostCategory.ENGINEERING));
	}

	@Test
	public void itPrintsAStringThatCanBeParsed() throws ParseException {
		assertThat(formatter.parse(
				formatter.print(PostCategory.ENGINEERING, null), null), equalTo(PostCategory.ENGINEERING));
	}

}
