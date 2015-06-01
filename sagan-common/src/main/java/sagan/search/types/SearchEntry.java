package sagan.search.types;

import org.springframework.security.crypto.codec.Base64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SearchEntry {

    public static final List<String> TYPES = Arrays.asList(SearchType.SITE_PAGE.toString(),
            SearchType.BLOG_POST.toString(), SearchType.GUIDE.toString(),
            SearchType.API_DOC.toString(), SearchType.REFERENCE_DOC.toString());


    private String path;

    private String title;

    private String subTitle;

    private String summary;

    private String rawContent;

    private List<String> facetPaths = new ArrayList<>();

    public abstract String getType();

    public String getId() {
        byte[] encodedId = Base64.encode(path.toLowerCase().getBytes());

        return new String(encodedId);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public List<String> getFacetPaths() {
        return facetPaths;
    }

    public void setFacetPaths(List<String> facetPaths) {
        this.facetPaths = facetPaths;
    }

    public void addFacetPaths(String... paths) {
        facetPaths.addAll(Arrays.asList(paths));
    }
}