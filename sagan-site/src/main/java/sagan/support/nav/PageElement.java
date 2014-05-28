package sagan.support.nav;

/**
 * Represents a single element in the array of pagination controls displayed on pages such
 * as the blog index or search results.
 *
 * An element is represented at the view layer by a label, and typically this value will
 * correspond to the target page number, but may also represent a collection of pages
 * beyond the scope of the current pagination control, usually represented by an
 * ellipsis ("...").
 *
 * A page element may be navigable or non-navigable, which translates at the view layer to
 * whether it is rendered as a link. For example, the current page is treated as
 * non-navigable as is any element representing "additional pages", i.e. the ellipsis
 * mentioned above.
 */
class PageElement {

    private final String label;
    private final boolean isNavigable;
    private final boolean isCurrentPage;

    public PageElement(long pageNumber, boolean isNavigable, boolean isCurrentPage) {
        this(pageNumber + "", isNavigable, isCurrentPage);
    }

    public PageElement(String label, boolean isNavigable, boolean isCurrentPage) {
        this.label = label;
        this.isNavigable = isNavigable;
        this.isCurrentPage = isCurrentPage;
    }

    public String getLabel() {
        return label;
    }

    public boolean isNavigable() {
        return isNavigable;
    }

    public boolean isCurrentPage() {
        return isCurrentPage;
    }
}
