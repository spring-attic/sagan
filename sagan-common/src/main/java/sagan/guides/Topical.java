package sagan.guides;

public class Topical extends AbstractGuide {

	private final static String TYPE_LABEL = "Topical Guide";

    // only used for JSON serialization
    public Topical() {
		this.setTypeLabel(TYPE_LABEL);
    }

    public Topical(GuideMetadata metadata) {
        super(metadata);
		this.setTypeLabel(TYPE_LABEL);
    }

}
