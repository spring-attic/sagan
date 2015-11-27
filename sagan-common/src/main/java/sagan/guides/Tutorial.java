package sagan.guides;

public class Tutorial extends AbstractGuide {

	private final static String TYPE_LABEL = "Tutorial";

    // only used for JSON serialization
    public Tutorial() {
		this.setTypeLabel(TYPE_LABEL);
    }

    public Tutorial(GuideMetadata metadata) {
        super(metadata);
		this.setTypeLabel(TYPE_LABEL);
    }

}
