package sagan.guides.support;

import sagan.guides.DocumentContent;

class AsciidocGuide implements DocumentContent {
    private final String content;
    private final String tableOfContents;

    public AsciidocGuide(String content, String tableOfContents) {
        this.content = content;
        this.tableOfContents = tableOfContents;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public String getTableOfContents() {
        return this.tableOfContents;
    }

}
