package sagan.guides.support;

class AsciidocGuide {
    private final String content;
    private final String tableOfContents;

    public AsciidocGuide(String content, String tableOfContents) {
        this.content = content;
        this.tableOfContents = tableOfContents;
    }

    public String getContent() {
        return this.content;
    }

    public String getTableOfContents() {
        return this.tableOfContents;
    }

}
