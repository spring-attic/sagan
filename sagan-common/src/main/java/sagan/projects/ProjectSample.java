package sagan.projects;

import javax.persistence.Embeddable;

@Embeddable
public class ProjectSample {

    private String title;
    private String description;
    private String url;
    private int displayOrder = Integer.MAX_VALUE;

    public ProjectSample() {
    }

    public ProjectSample(String title, int displayOrder) {
        this.title = title;
        this.setDisplayOrder(displayOrder);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
