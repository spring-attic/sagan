package org.springframework.site.web;

import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.social.github.api.GitHubRepo;

/**
 * Convenient helper for view rendering.
 * @author Dave Syer
 * 
 */
public class ViewRenderingHelper {

	public String navClass(String active, String current) {
		if (active.equals(current)) {
			return "navbar-link active";
		} else {
			return "navbar-link";
		}
	}

	public String blogClass(String active, String current) {
		if (active.equals(current)) {
			return "blog-category active";
		} else {
			return "blog-category";
		}
	}

	public String extractTitleFromRepoDescription(GitHubRepo repo) {
		return extractTitleFromDescription(repo.getDescription());
	}

	public String extractTitleFromRepoDescription(GettingStartedGuide repo) {
		return extractTitleFromDescription(repo.getDescription());
	}

	public String extractSubtitleFromRepoDescription(GettingStartedGuide repo) {
		return extractSubtitleFromDescription(repo.getDescription());
	}

	public String extractSubtitleFromRepoDescription(GitHubRepo repo) {
		return extractSubtitleFromDescription(repo.getDescription());
	}

	private String extractTitleFromDescription(String description) {
		String[] split = description.split("::", 2);
		return split[0].trim();
	}

	private String extractSubtitleFromDescription(String description) {
		String[] split = description.split("::", 2);
		return (split.length > 1) ? split[1].trim() : "";
	}

}
