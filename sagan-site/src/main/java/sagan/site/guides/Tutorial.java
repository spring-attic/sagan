package sagan.site.guides;

import sagan.site.renderer.GuideContent;

public class Tutorial extends AbstractGuide {

	private final static String TYPE_LABEL = "Tutorial";

    // only used for JSON serialization
    public Tutorial() {
		this.setTypeLabel(TYPE_LABEL);
    }

    public Tutorial(GuideHeader header, GuideContent content) {
        super(TYPE_LABEL, header, content);
    }

}
