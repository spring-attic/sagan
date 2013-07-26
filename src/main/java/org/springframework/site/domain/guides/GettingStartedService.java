package org.springframework.site.domain.guides;

import java.util.List;

public interface GettingStartedService {

	GettingStartedGuide loadGuide(String guideId);

	List<GettingStartedGuide> listGuides();

	byte[] loadImage(String guideId, String imageName);
}
