package org.springframework.site.domain.guides;

import java.util.List;

public interface GuidesService {

	Guide loadGettingStartedGuide(String guideId);
	Guide loadTutorial(String tutorialId);

	List<Guide> listGettingStartedGuides();

	List<Guide> listTutorials();

	byte[] loadGettingStartedImage(String guideId, String imageName);

	Guide loadTutorialPage(String tutorialId, String pagePath);

	byte[] loadTutorialImage(String tutorialId, String imageName);
}
