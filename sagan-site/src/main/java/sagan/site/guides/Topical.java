package sagan.site.guides;

import sagan.site.renderer.GuideContent;

public class Topical extends AbstractGuide {

	private final static String TYPE_LABEL = "Topical Guide";

    // only used for JSON serialization
    public Topical() {
		this.setTypeLabel(TYPE_LABEL);
    }

    public Topical(GuideHeader metadata, GuideContent content) {
        super(TYPE_LABEL, metadata, content);
    }

}
