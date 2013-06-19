package org.springframework.site.blog;

import org.junit.Test;
import org.springframework.data.domain.Sort;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BlogPostsPageRequestTests {

	@Test
	public void defaultPageSizeIs10() {
		assertThat(new BlogPostsPageRequest(1).getPageSize(), is(10));
	}

	@Test
	public void defaultSortOrderIsDescendingByCreatedDate() {
		assertThat(new BlogPostsPageRequest(1).getSort(), is(new Sort(Sort.Direction.DESC, "createdDate")));
	}

}
