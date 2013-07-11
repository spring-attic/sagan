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
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

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
		given(this.gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/some/path";
		String htmlResponse = "<h1>this is a header</h1>";
		GuideHtml response = new GuideHtml(htmlResponse);
		@SuppressWarnings("unchecked")
		Class<GuideHtml> anyObject = (Class<GuideHtml>) anyObject();
		given(this.restOperations.getForObject(anyString(), anyObject)).willReturn(
				response);
		String html = new GitHubService(this.gitHub).getRawFileAsHtml(filePath);

		verify(this.restOperations).getForObject(eq(GitHubService.HOSTNAME + filePath),
				eq(GuideHtml.class));
		assertThat(html, is(htmlResponse));
	}
}
