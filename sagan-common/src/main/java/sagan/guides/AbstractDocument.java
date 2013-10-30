package sagan.guides;

import org.springframework.util.Assert;

class AbstractDocument implements Document {

    private final ContentProvider contentProvider;
    private String content;
    private String sidebar;

    public AbstractDocument(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public void setContent(String body) {
        this.content = body;
    }

    public void setSidebar(String sidebar) {
        this.sidebar = sidebar;
    }

    public String getContent() {
        if (content == null) {
            contentProvider.populate(this);
        }
        // body must have at least some text to be valid
        Assert.hasText(content, "Expected body content to be populated");
        return content;
    }

    public String getSidebar() {
        if (sidebar == null) {
            contentProvider.populate(this);
        }
        // sidebar can be empty string but cannot be null
        Assert.notNull(sidebar, "Expected sidebar content to be populated");
        return sidebar;
    }

}
