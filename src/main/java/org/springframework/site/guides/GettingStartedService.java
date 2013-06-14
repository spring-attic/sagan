package org.springframework.site.guides;

import java.util.List;

public interface GettingStartedService {

	String loadGuide(String guideId);

	List<Guide> listGuides();

	byte[] loadImage(String guideSlug, String imageName);
}
