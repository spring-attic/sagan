package sagan.guides.support;

import sagan.guides.DocumentContent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class AsciidocGuide implements DocumentContent {

    private final String content;
    private final String tableOfContents;

    @JsonCreator
    public AsciidocGuide(@JsonProperty("content") String content,
                         @JsonProperty("tableOfContents") String tableOfContents) {
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
