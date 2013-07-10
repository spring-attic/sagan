package org.springframework.site.domain.guides;

import java.util.List;

public interface GettingStartedService {

	GettingStartedGuide loadGuide(String guideId);

	List<GuideRepo> listGuides();

	byte[] loadImage(String guideSlug, String imageName);
}
