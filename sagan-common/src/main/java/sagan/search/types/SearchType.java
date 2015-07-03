package sagan.search.types;

public enum SearchType {

    SITE_PAGE("sitepage"), BLOG_POST("blogpost"), GUIDE("guidedoc"),
    API_DOC("apidoc"), REFERENCE_DOC("referencedoc"), PROJECT_PAGE("projectpage");

    private final String name;

    SearchType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
