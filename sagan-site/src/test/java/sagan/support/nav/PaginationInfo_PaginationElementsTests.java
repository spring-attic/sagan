package sagan.support.nav;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaginationInfo_PaginationElementsTests {

    List<String> content = new ArrayList<>();

    @Test
    public void givenOnePage_rendersCurrentElement() {
        PageRequest pageRequest = new PageRequest(0, 10);
        int itemCount = 3;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        List<PageElement> pageElements = paginationInfo.getPageElements();
        assertThat(pageElements.size(), is(equalTo(1)));
        assertNotNavigableElementOnPage("1", pageElements.get(0));
    }

    @Test
    public void givenTwoPages_rendersCurrentElementAndPageTwo() {
        PageRequest pageRequest = new PageRequest(0, 10);
        int itemCount = 13;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(2)));
        assertNotNavigableElementOnPage("1", pageElements.get(0));
        assertNavigableElementOnPage("2", pageElements.get(1));
    }

    @Test
    public void givenThreePagesOnPageTwo_rendersCurrentElementAndPageTwo() {
        PageRequest pageRequest = new PageRequest(1, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(pageElements.size(), is(equalTo(3)));
        assertNavigableElementOnPage("1", pageElements.get(0));
        assertNotNavigableElementOnPage("2", pageElements.get(1));
        assertNavigableElementOnPage("3", pageElements.get(2));
    }

    @Test
    public void givenTenPagesOnPageFive_rendersPreviousTwoPagesAndNextThree() {
        PageRequest pageRequest = new PageRequest(4, 10);
        int itemCount = 93;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(10)));

        assertNavigableElementOnPage("3", pageElements.get(2));
        assertNavigableElementOnPage("4", pageElements.get(3));
        assertNotNavigableElementOnPage("5", pageElements.get(4));
        assertNavigableElementOnPage("6", pageElements.get(5));
        assertNavigableElementOnPage("7", pageElements.get(6));
        assertNavigableElementOnPage("8", pageElements.get(7));
    }

    @Test
    public void alwaysRendersFirstAndLastPage() {
        PageRequest pageRequest = new PageRequest(4, 10);
        int itemCount = 93;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(10)));

        assertNavigableElementOnPage("1", pageElements.get(0));
        assertNavigableElementOnPage("10", pageElements.get(9));
    }

    @Test
    public void rendersEllipsesBetweenNonAdjacentPages() {
        PageRequest pageRequest = new PageRequest(4, 10);
        int itemCount = 93;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(10)));

        assertNotNavigableElementOnPage("...", pageElements.get(1));
        assertNotNavigableElementOnPage("...", pageElements.get(8));
    }

    @Test
    public void doesNotRenderEllipsesBetweenAdjacentPages() {
        PageRequest pageRequest = new PageRequest(2, 10);
        int itemCount = 63;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(7)));

        assertNavigableElementOnPage("1", pageElements.get(0));
        assertNavigableElementOnPage("2", pageElements.get(1));
        assertNotNavigableElementOnPage("3", pageElements.get(2));
        assertNavigableElementOnPage("4", pageElements.get(3));
        assertNavigableElementOnPage("5", pageElements.get(4));
        assertNavigableElementOnPage("6", pageElements.get(5));
        assertNavigableElementOnPage("7", pageElements.get(6));
    }

    @Test
    public void rendersFirstSixPagesOnPageOne_givenEnoughNumberOfPages() throws Exception {
        int currentPageIndex = 0;

        PageRequest pageRequest = new PageRequest(currentPageIndex, 10);
        int itemCount = 133;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(8)));

        assertNotNavigableElementOnPage("1", pageElements.get(0));
        assertNavigableElementOnPage("2", pageElements.get(1));
        assertNavigableElementOnPage("3", pageElements.get(2));
        assertNavigableElementOnPage("4", pageElements.get(3));
        assertNavigableElementOnPage("5", pageElements.get(4));
        assertNavigableElementOnPage("6", pageElements.get(5));
        assertNotNavigableElementOnPage("...", pageElements.get(6));
        assertNavigableElementOnPage("14", pageElements.get(7));
    }

    @Test
    public void rendersFirstSixPagesOnPageThree_givenEnoughNumberOfPages() throws Exception {
        int currentPageIndex = 2;

        PageRequest pageRequest = new PageRequest(currentPageIndex, 10);
        int itemCount = 133;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(paginationInfo.getPageElements().size(), is(equalTo(8)));

        assertNavigableElementOnPage("1", pageElements.get(0));
        assertNavigableElementOnPage("2", pageElements.get(1));
        assertNotNavigableElementOnPage("3", pageElements.get(2));
        assertNavigableElementOnPage("4", pageElements.get(3));
        assertNavigableElementOnPage("5", pageElements.get(4));
        assertNavigableElementOnPage("6", pageElements.get(5));
        assertNotNavigableElementOnPage("...", pageElements.get(6));
        assertNavigableElementOnPage("14", pageElements.get(7));
    }

    @Test
    public void rendersCurrentPage() throws Exception {
        int currentPageIndex = 2;

        PageRequest pageRequest = new PageRequest(currentPageIndex, 10);
        int itemCount = 133;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));
        List<PageElement> pageElements = paginationInfo.getPageElements();

        assertThat(pageElements.get(0).isCurrentPage(), is(false));
        assertThat(pageElements.get(1).isCurrentPage(), is(false));
        assertThat(pageElements.get(2).isCurrentPage(), is(true));
        assertThat(pageElements.get(3).isCurrentPage(), is(false));
        assertThat(pageElements.get(4).isCurrentPage(), is(false));
        assertThat(pageElements.get(5).isCurrentPage(), is(false));
        assertThat(pageElements.get(6).isCurrentPage(), is(false));
        assertThat(pageElements.get(7).isCurrentPage(), is(false));
    }

    private void assertNotNavigableElementOnPage(String page, PageElement element) {
        assertThat(element.isNavigable(), is(false));
        assertThat(element.getLabel(), is(page));
    }

    private void assertNavigableElementOnPage(String page, PageElement element) {
        assertThat(element.isNavigable(), is(true));
        assertThat(element.getLabel(), is(page));
    }
}
