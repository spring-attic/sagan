package sagan.search.support;

import sagan.search.types.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchEntryBuilder {

    private String title = "A random title";
    private String rawContent = "A random post content";
    private String summary = "A random post summary";
    private Date publishAt = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    private String path = "/some/path";
    private List<String> facetPaths = new ArrayList<>();
    private boolean current = true;
    private String version;
    private String projectId;
    private String className;
    private String packageName;


    private SearchEntryBuilder() {
    }

    public static SearchEntryBuilder entry() {
        return new SearchEntryBuilder();
    }

    public SearchEntryBuilder title(String title) {
        this.title = title;
        return this;
    }

    public SearchEntryBuilder rawContent(String rawContent) {
        this.rawContent = rawContent;
        return this;
    }

    public SearchEntryBuilder summary(String summary) {
        this.summary = summary;
        return this;
    }

    public SearchEntryBuilder publishAt(Date date) {
        publishAt = date;
        return this;
    }

    public SearchEntryBuilder publishAt(String dateString) throws ParseException {
        publishAt = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateString);
        return this;
    }

    public SearchEntryBuilder path(String path) {
        this.path = path;
        return this;
    }

    public SearchEntryBuilder facetPath(String facetPath) {
        facetPaths.add(facetPath);
        return this;
    }

    public SearchEntryBuilder notCurrent() {
        current = false;
        return this;
    }

    public SearchEntryBuilder version(String version) {
        this.version = version;
        return this;
    }

    public SearchEntryBuilder projectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public SearchEntryBuilder className(String className) {
        this.className = className;
        return this;
    }

    public SearchEntryBuilder packageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public BlogPost blog() {
        BlogPost blogPost = build(new BlogPost());
        blogPost.setPublishAt(this.publishAt);
        return blogPost;
    }

    public ApiDoc api() {
        ApiDoc api = build(new ApiDoc());
        api.setClassName(this.className);
        api.setPackageName(this.packageName);
        api.setCurrent(this.current);
        api.setVersion(this.version);
        api.setProjectId(this.projectId);
        return api;
    }

    public GuideDoc guide() {
        GuideDoc guide = build(new GuideDoc());
        return guide;
    }

    public ReferenceDoc reference() {
        ReferenceDoc ref = build(new ReferenceDoc());
        ref.setCurrent(this.current);
        ref.setVersion(this.version);
        ref.setProjectId(this.projectId);
        return ref;
    }

    public SitePage sitePage() {
        SitePage page = build(new SitePage());
        return page;
    }

    public ProjectPage projectPage() {
        ProjectPage page = build(new ProjectPage());
        return page;
    }

    private <R extends SearchEntry> R build(R searchEntry) {
        searchEntry.setTitle(title);
        searchEntry.setRawContent(rawContent);
        searchEntry.setSummary(summary);
        searchEntry.setPath(path);
        searchEntry.setFacetPaths(facetPaths);
        return searchEntry;
    }

}
