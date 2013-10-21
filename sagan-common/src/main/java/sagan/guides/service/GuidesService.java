package sagan.guides.service;

import sagan.guides.Guide;
import sagan.guides.GuideWithoutContent;

import java.util.List;

public interface GuidesService {

    Guide loadGettingStartedGuide(String guideId);

    Guide loadTutorial(String tutorialId);

    Guide loadTutorialPage(String tutorialId, String pagePath);

    List<GuideWithoutContent> listGettingStartedGuidesWithoutContent();

    List<GuideWithoutContent> listTutorialsWithoutContent();

    List<Guide> listGettingStartedGuides();

    List<Guide> listTutorials();

    byte[] loadGettingStartedImage(String guideId, String imageName);

    byte[] loadTutorialImage(String tutorialId, String imageName);
}
