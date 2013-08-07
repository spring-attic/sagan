package org.springframework.site.domain.understanding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;

@Service
public class UnderstandingGuidesService {

	private static final String CONTENT_PATH = "/repos/springframework-meta/understanding/contents/%s/README.md";
	private static final String SIDEBAR_PATH = "/repos/springframework-meta/understanding/contents/%s/SIDEBAR.md";

	private final GitHubService gitHubService;

	@Autowired
	public UnderstandingGuidesService(GitHubService gitHubService) {
		this.gitHubService = gitHubService;
	}

	public UnderstandingGuide getGuideForSubject(String subject) {
		return new UnderstandingGuide(getContent(subject), getSidebar(subject));
	}

	private String getContent(String subject) {
		try {
			return this.gitHubService.getRawFileAsHtml(String.format(CONTENT_PATH, subject));
		} catch (RestClientException e) {
			String msg = String.format("No getting started guide found for '%s'", subject);
			throw new UnderstandingGuideNotFoundException(msg, e);
		}
	}

	private String getSidebar(String subject) {
		try {
			return this.gitHubService.getRawFileAsHtml(String.format(SIDEBAR_PATH, subject));
		} catch (RestClientException e) {
			return "";
		}
	}

	public Iterable<UnderstandingGuide> getGuidesList() {
		return Arrays.asList(new UnderstandingGuide("content", "sidebar"));
	}
}
