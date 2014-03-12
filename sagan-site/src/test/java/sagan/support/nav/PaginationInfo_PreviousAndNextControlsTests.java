package sagan.support.nav;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PaginationInfo_PreviousAndNextControlsTests {

    List<String> content = new ArrayList<>();

    @Test
    public void givenOnePage_controlsAreNotVisible() {
        PageRequest pageRequest = new PageRequest(0, 10);
        int itemCount = 8;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible(), is(false));
        assertThat(paginationInfo.isPreviousVisible(), is(false));
        assertThat(paginationInfo.isNextVisible(), is(false));
    }

    @Test
    public void givenOnFirstPageOfThree_nextIsVisible() {
        PageRequest pageRequest = new PageRequest(0, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible(), is(true));
        assertThat(paginationInfo.isPreviousVisible(), is(false));
        assertThat(paginationInfo.isNextVisible(), is(true));
    }

    @Test
    public void givenOnSecondPageOfThree_nextAndPreviousAreVisible() {
        PageRequest pageRequest = new PageRequest(1, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible(), is(true));
        assertThat(paginationInfo.isPreviousVisible(), is(true));
        assertThat(paginationInfo.isNextVisible(), is(true));
    }

    @Test
    public void givenOnThirdPageOfThree_previousIsVisible() {
        PageRequest pageRequest = new PageRequest(2, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible(), is(true));
        assertThat(paginationInfo.isPreviousVisible(), is(true));
        assertThat(paginationInfo.isNextVisible(), is(false));
    }

}
