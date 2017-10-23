package sagan.guides;

public class UnderstandingDoc implements UnderstandingMetadata, DocumentContent {

    private String subject;
    private String content;
	private String tableOfContents;

    // only used for JSON serialization
    public UnderstandingDoc() {
    }

    public UnderstandingDoc(String subject) {
        this.subject = subject;
    }

    public UnderstandingDoc(String subject, final String content) {
        this.subject = subject;
        this.content = content;
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
	public String getTableOfContents() {
		return this.tableOfContents;
	}

	public void setTableOfContents(String tableOfContents) {
		this.tableOfContents = tableOfContents;
	}
}
