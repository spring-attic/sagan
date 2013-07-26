package org.springframework.site.web;

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
		String[] split = repo.getDescription().split("::", 2);
		return split[0].trim();
	}

	public String extractSubtitleFromRepoDescription(GitHubRepo repo) {
		String[] split = repo.getDescription().split("::", 2);
		return (split.length > 1) ? split[1].trim() : "";
	}

}
