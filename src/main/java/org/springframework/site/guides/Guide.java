package org.springframework.site.guides;

import org.springframework.social.github.api.GitHubRepo;

public class Guide extends GitHubRepo {

	public String getGuideId(){
		return getName().replaceAll("^gs-", "");
	}

	public boolean isGettingStartedGuide() {
		return getName().startsWith("gs-");
	}
}
