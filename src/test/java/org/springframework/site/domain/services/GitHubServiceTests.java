package org.springframework.site.domain.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.site.domain.guides.GuideHtml;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestOperations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitHubServiceTests {

	@Mock
	GitHub gitHub;

	@Mock
	RestOperations restOperations;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void convertRawFileToHtml() {
		when(gitHub.restOperations()).thenReturn(restOperations);

		String filePath = "/some/path";
		String htmlResponse = "<h1>this is a header</h1>";
		GuideHtml response = new GuideHtml(htmlResponse);
		when(restOperations.getForObject(anyString(), any(Class.class))).thenReturn(response);
		String html = new GitHubService(gitHub).getRawFileAsHtml(filePath);

		verify(restOperations).getForObject(eq(GitHubService.HOSTNAME + filePath), eq(GuideHtml.class));
		assertThat(html, is(htmlResponse));
	}
}
