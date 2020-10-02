package sagan.site.blog;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class PostCategoryFormatterTests {
	private PostCategoryFormatter formatter = new PostCategoryFormatter();

	@Test
	public void itConvertsUrlSlugStringsToPostCategories() throws ParseException {
		assertThat(formatter.parse(PostCategory.ENGINEERING.getUrlSlug(), null)).isEqualTo(PostCategory.ENGINEERING);
	}

	@Test
	public void itConvertsEnumNameStringsToPostCategories() throws ParseException {
		assertThat(formatter.parse(PostCategory.ENGINEERING.name(), null)).isEqualTo(PostCategory.ENGINEERING);
	}

	@Test
	public void itPrintsAStringThatCanBeParsed() throws ParseException {
		assertThat(formatter.parse(formatter.print(PostCategory.ENGINEERING, null), null)).isEqualTo(PostCategory.ENGINEERING);
	}

}
