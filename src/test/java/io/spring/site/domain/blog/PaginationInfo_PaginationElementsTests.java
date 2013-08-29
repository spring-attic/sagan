package io.spring.site.domain.blog;

import io.spring.site.web.PageElement;
import io.spring.site.web.PaginationInfo;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaginationInfo_PaginationElementsTests {

	List<String> content = new ArrayList<>();

	@Test
	public void givenOnePage_rendersCurrentElement() {
		PageRequest pageRequest = new PageRequest(0, 10);
		int itemCount = 3;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));

		List<PageElement> pageElements = paginationInfo.getPageElements();
		assertThat(pageElements.size(), is(equalTo(1)));
		assertInactiveElementOnPage("1", pageElements.get(0));
	}

	@Test
	public void givenTwoPages_rendersCurrentElementAndPageTwo() {
		PageRequest pageRequest = new PageRequest(0, 10);
		int itemCount = 13;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(2)));
		assertInactiveElementOnPage("1", pageElements.get(0));
		assertActiveElementOnPage("2", pageElements.get(1));
	}

	@Test
	public void givenThreePagesOnPageTwo_rendersCurrentElementAndPageTwo() {
		PageRequest pageRequest = new PageRequest(1, 10);
		int itemCount = 23;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(pageElements.size(), is(equalTo(3)));
		assertActiveElementOnPage("1", pageElements.get(0));
		assertInactiveElementOnPage("2", pageElements.get(1));
		assertActiveElementOnPage("3", pageElements.get(2));
	}

	@Test
	public void givenTenPagesOnPageFive_rendersPreviousTwoPagesAndNextThree() {
		PageRequest pageRequest = new PageRequest(4, 10);
		int itemCount = 93;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(10)));

		assertActiveElementOnPage("3", pageElements.get(2));
		assertActiveElementOnPage("4", pageElements.get(3));
		assertInactiveElementOnPage("5", pageElements.get(4));
		assertActiveElementOnPage("6", pageElements.get(5));
		assertActiveElementOnPage("7", pageElements.get(6));
		assertActiveElementOnPage("8", pageElements.get(7));
	}

	@Test
	public void alwaysRendersFirstAndLastPage() {
		PageRequest pageRequest = new PageRequest(4, 10);
		int itemCount = 93;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(10)));

		assertActiveElementOnPage("1", pageElements.get(0));
		assertActiveElementOnPage("10", pageElements.get(9));
	}

	@Test
	public void rendersEllipsesBetweenNonAdjacentPages() {
		PageRequest pageRequest = new PageRequest(4, 10);
		int itemCount = 93;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(10)));

		assertInactiveElementOnPage("...", pageElements.get(1));
		assertInactiveElementOnPage("...", pageElements.get(8));
	}

	@Test
	public void doesNotRenderEllipsesBetweenAdjacentPages() {
		PageRequest pageRequest = new PageRequest(2, 10);
		int itemCount = 63;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(7)));

		assertActiveElementOnPage("1", pageElements.get(0));
		assertActiveElementOnPage("2", pageElements.get(1));
		assertInactiveElementOnPage("3", pageElements.get(2));
		assertActiveElementOnPage("4", pageElements.get(3));
		assertActiveElementOnPage("5", pageElements.get(4));
		assertActiveElementOnPage("6", pageElements.get(5));
		assertActiveElementOnPage("7", pageElements.get(6));
	}

	@Test
	public void rendersFirstSixPagesOnPageOne_givenEnoughNumberOfPages() throws Exception {
		int currentPageIndex = 0;

		PageRequest pageRequest = new PageRequest(currentPageIndex, 10);
		int itemCount = 133;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(8)));

		assertInactiveElementOnPage("1", pageElements.get(0));
		assertActiveElementOnPage("2", pageElements.get(1));
		assertActiveElementOnPage("3", pageElements.get(2));
		assertActiveElementOnPage("4", pageElements.get(3));
		assertActiveElementOnPage("5", pageElements.get(4));
		assertActiveElementOnPage("6", pageElements.get(5));
		assertInactiveElementOnPage("...", pageElements.get(6));
		assertActiveElementOnPage("14", pageElements.get(7));
	}

	@Test
	public void rendersFirstSixPagesOnPageThree_givenEnoughNumberOfPages() throws Exception {
		int currentPageIndex = 2;

		PageRequest pageRequest = new PageRequest(currentPageIndex, 10);
		int itemCount = 133;
		PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(this.content, pageRequest, itemCount));
		List<PageElement> pageElements = paginationInfo.getPageElements();

		assertThat(paginationInfo.getPageElements().size(), is(equalTo(8)));

		assertActiveElementOnPage("1", pageElements.get(0));
		assertActiveElementOnPage("2", pageElements.get(1));
		assertInactiveElementOnPage("3", pageElements.get(2));
		assertActiveElementOnPage("4", pageElements.get(3));
		assertActiveElementOnPage("5", pageElements.get(4));
		assertActiveElementOnPage("6", pageElements.get(5));
		assertInactiveElementOnPage("...", pageElements.get(6));
		assertActiveElementOnPage("14", pageElements.get(7));
	}

	private void assertInactiveElementOnPage(String page, PageElement element) {
		assertThat(element.isActive(), is(false));
		assertThat(element.getPageText(), is(page));
	}

	private void assertActiveElementOnPage(String page, PageElement element) {
		assertThat(element.isActive(), is(true));
		assertThat(element.getPageText(), is(page));
	}
}
