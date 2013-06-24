package org.springframework.site.guides;

import org.springframework.social.github.api.GitHubRepo;

@SuppressWarnings("serial")
public class GuideRepo extends GitHubRepo {

	public String getGuideId(){
		return getName().replaceAll("^gs-", "");
	}

	public boolean isGettingStartedGuide() {
		return getName().startsWith("gs-");
	}
}
