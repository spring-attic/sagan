package sagan.support.nav;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Utility methods for use when creating requests for pages of information, e.g. when
 * rendering lists of posts on the blog index or individual team member pages.
 */
public abstract class PageableFactory {

    public static Pageable all() {
        return build(0, Integer.MAX_VALUE);
    }

    public static Pageable forLists(int page) {
        return build(page - 1, 10);
    }

    public static Pageable forDashboard(int page) {
        return build(page - 1, 30);
    }

    public static Pageable forFeeds() {
        return build(0, 20);
    }

    public static Pageable forSearch(int page) {
        return new PageRequest(page - 1, 10);
    }

    private static Pageable build(int page, int pageSize) {
        return new PageRequest(page, pageSize, Sort.Direction.DESC, "publishAt");
    }
}
