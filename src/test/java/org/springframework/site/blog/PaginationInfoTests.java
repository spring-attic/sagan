package org.springframework.site.blog;


import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaginationInfoTests {

	@Test
	public void givenOnePage_controlsAreNotVisible() {
		assertThat(new PaginationInfo(1, 1).isVisible(), is(false));
		assertThat(new PaginationInfo(1, 1).isPreviousVisible(), is(false));
		assertThat(new PaginationInfo(1, 1).isNextVisible(), is(false));
	}

	@Test
	public void givenOnFirstPageOfThree_nextIsVisible() {
		assertThat(new PaginationInfo(1, 3).isVisible(), is(true));
		assertThat(new PaginationInfo(1, 3).isPreviousVisible(), is(false));
		assertThat(new PaginationInfo(1, 3).isNextVisible(), is(true));
	}

	@Test
	public void givenOnSecondPageOfThree_nextAndPreviousAreVisible() {
		assertThat(new PaginationInfo(2, 3).isVisible(), is(true));
		assertThat(new PaginationInfo(2, 3).isPreviousVisible(), is(true));
		assertThat(new PaginationInfo(2, 3).isNextVisible(), is(true));
	}

	@Test
	public void givenOnThirdPageOfThree_previousIsVisible() {
		assertThat(new PaginationInfo(3, 3).isVisible(), is(true));
		assertThat(new PaginationInfo(3, 3).isPreviousVisible(), is(true));
		assertThat(new PaginationInfo(3, 3).isNextVisible(), is(false));
	}

	@Test
	public void givenOnPageTwo_nextPageIsThree() {
		assertThat(new PaginationInfo(2, 3).getNextPageNumber(), is(equalTo(3L)));
	}

	@Test
	public void givenOnPageTwo_previousPageIsOne() {
		assertThat(new PaginationInfo(2, 3).getPreviousPageNumber(), is(equalTo(1L)));
	}


}
