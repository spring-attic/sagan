package sagan.guides;

public class GettingStartedGuide extends AbstractGuide {

	private final static String TYPE_LABEL = "Getting Started";

    // only used for JSON serialization
    public GettingStartedGuide() {
		this.setTypeLabel(TYPE_LABEL);
    }

     public GettingStartedGuide(GuideMetadata metadata) {
        super(metadata);
		this.setTypeLabel(TYPE_LABEL);
    }
}
