package org.springframework.site.domain.guides;

import java.util.List;

public interface GuidesService {

	GettingStartedGuide loadGuide(String guideId);

	List<GettingStartedGuide> listGettingStartedGuides();

	List<GettingStartedGuide> listTutorials();

	byte[] loadImage(String guideId, String imageName);
}
