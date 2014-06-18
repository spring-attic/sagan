package sagan.search;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.crypto.codec.Base64;

public class SearchEntry {

    private String path;

    private String summary;

    private String rawContent;

    private String title;

    private String subTitle;

    private List<String> facetPaths = new ArrayList<>();

    private boolean current = true;

    // TODO: maybe we don't need this in the index?
    private LocalDateTime publishAt = LocalDateTime.now();
    private String type = "site";
    private String version;
    private String projectId;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public LocalDateTime getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(LocalDateTime publishAt) {
        this.publishAt = publishAt;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getId() {
        byte[] encodedId = Base64.encode(path.toLowerCase().getBytes());
        return new String(encodedId);
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}
