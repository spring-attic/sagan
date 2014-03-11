package sagan.blog.support;

import sagan.blog.PostCategory;

import java.text.ParseException;

import org.junit.Test;

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
        assertThat(formatter.parse(formatter.print(PostCategory.ENGINEERING, null), null),
                equalTo(PostCategory.ENGINEERING));
    }

}
