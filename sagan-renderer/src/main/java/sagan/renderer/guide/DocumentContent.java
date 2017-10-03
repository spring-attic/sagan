package sagan.renderer.guide;

/**
 * Document content holder.
 */
public class DocumentContent {

    private String tableOfContents;
    private String content;

    public DocumentContent(String content, String tableOfContents) {
        this.content = content;
        this.tableOfContents = tableOfContents;
    }

    public DocumentContent() {
    }

    public void setTableOfContents(String tableOfContents) {
        this.tableOfContents = tableOfContents;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public String getTableOfContents() {
        return this.tableOfContents;
    }

}
