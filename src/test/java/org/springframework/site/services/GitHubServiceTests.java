package org.springframework.site.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestOperations;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
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
		Map<String, String> response = new HashMap<String, String>();
		response.put("content", "IyB0aGlzIGlzIGEgaGVhZGVy");
		when(restOperations.getForObject(eq(GitHubService.HOSTNAME + filePath), eq(Map.class))).thenReturn(response);

		String htmlResponse = "<h1>this is a header</h1>";
		when(restOperations.postForObject(anyString(), eq("# this is a header"), eq(String.class))).thenReturn(htmlResponse);

		String html = new GitHubService(gitHub).getRawFileAsHtml(filePath);

		assertThat(html, is(htmlResponse));
	}
}
