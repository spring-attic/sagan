package sagan.guides;

public class UnderstandingDoc extends AbstractDocument {

    private final String subject;

    public UnderstandingDoc(String subject, ContentProvider<UnderstandingDoc> contentProvider) {
        super(contentProvider);
        this.subject = subject;
    }

    public UnderstandingDoc(String subject, final String content, final String sidebar) {
        super((ContentProvider<UnderstandingDoc>) (doc) -> {
            doc.setContent(content);
            doc.setSidebar(sidebar);
        });
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

}
