package sagan.guides;

public class UnderstandingDoc implements UnderstandingMetadata, DocumentContent {

    private String subject;
    private String content;
    private String sidebar;

    // only used for JSON serialization
    public UnderstandingDoc() {
    }

    public UnderstandingDoc(String subject) {
        this.subject = subject;
    }

    public UnderstandingDoc(String subject, final String content, final String sidebar) {
        this.subject = subject;
        this.content = content;
        this.sidebar = sidebar;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getSidebar() {
        return sidebar;
    }

    public void setSidebar(String sidebar) {
        this.sidebar = sidebar;
    }

}
