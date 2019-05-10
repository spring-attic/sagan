package sagan.site.guides;

import sagan.site.renderer.GuideContent;

public class GettingStartedGuide extends AbstractGuide {

	private final static String TYPE_LABEL = "Getting Started";

    // only used for JSON serialization
    public GettingStartedGuide() {
		this.setTypeLabel(TYPE_LABEL);
    }

     public GettingStartedGuide(GuideHeader header, GuideContent content) {
        super(TYPE_LABEL, header, content);
    }
}
