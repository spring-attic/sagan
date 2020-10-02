package sagan.support.nav;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;


public class PaginationInfo_PreviousAndNextControlsTests {

    List<String> content = new ArrayList<>();

    @Test
    public void givenOnePage_controlsAreNotVisible() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        int itemCount = 8;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible()).isFalse();
        assertThat(paginationInfo.isPreviousVisible()).isFalse();
        assertThat(paginationInfo.isNextVisible()).isFalse();
    }

    @Test
    public void givenOnFirstPageOfThree_nextIsVisible() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible()).isTrue();
        assertThat(paginationInfo.isPreviousVisible()).isFalse();
        assertThat(paginationInfo.isNextVisible()).isTrue();
    }

    @Test
    public void givenOnSecondPageOfThree_nextAndPreviousAreVisible() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible()).isTrue();
        assertThat(paginationInfo.isPreviousVisible()).isTrue();
        assertThat(paginationInfo.isNextVisible()).isTrue();
    }

    @Test
    public void givenOnThirdPageOfThree_previousIsVisible() {
        PageRequest pageRequest = PageRequest.of(2, 10);
        int itemCount = 23;
        PaginationInfo paginationInfo = new PaginationInfo(new PageImpl<>(content, pageRequest, itemCount));

        assertThat(paginationInfo.isVisible()).isTrue();
        assertThat(paginationInfo.isPreviousVisible()).isTrue();
        assertThat(paginationInfo.isNextVisible()).isFalse();
    }

}
