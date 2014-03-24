package sagan.guides;

import org.springframework.util.Assert;

@SuppressWarnings("rawtypes")
class AbstractDocument implements Document {

    private final ContentProvider contentProvider;
    private String content;
    private String sidebar;

    public AbstractDocument(ContentProvider contentProvider) {
        this.contentProvider = contentProvider;
    }

    public void setContent(String body) {
        content = body;
    }

    public void setSidebar(String sidebar) {
        this.sidebar = sidebar;
    }

    @SuppressWarnings("unchecked")
    public String getContent() {
        if (content == null) {
            contentProvider.populate(this);
        }
        // body must have at least some text to be valid
        Assert.hasText(content, "Expected body content to be populated");
        return content;
    }

    @SuppressWarnings("unchecked")
    public String getSidebar() {
        if (sidebar == null) {
            contentProvider.populate(this);
        }
        // sidebar can be empty string but cannot be null
        Assert.notNull(sidebar, "Expected sidebar content to be populated");
        return sidebar;
    }

}
