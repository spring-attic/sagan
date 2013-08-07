package org.springframework.site.domain.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.site.domain.guides.GuideHtml;
import org.springframework.social.github.api.GitHub;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

public class GitHubServiceTests {

	private static final String CACHED_CONTENT = "<h1>cached document</h1>";
	@Mock
	GitHub gitHub;

	@Mock
	RestOperations restOperations;

	CacheService cacheService = new InMemoryCacheService();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		cacheService.cacheContent("/cached/path", "a cached etag", CACHED_CONTENT);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertRawFileToHtml_notCached_returnsContentFromGithub_andCaches() {
		given(gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/not_cached/path";
		String htmlResponse = "<h1>this is a header</h1>";
		String etag = "\"etagToCache\"";

		stubResponseOK(htmlResponse, etag);

		String html = getRawFileAsHtml(filePath);

		verify(restOperations).exchange(anyString(), (HttpMethod) anyObject(), (HttpEntity) anyObject(), (Class<GuideHtml>) anyObject());

		assertThat(html, is(htmlResponse));
		assertThat(cacheService.getContentForPath("/not_cached/path"), is(htmlResponse));
		assertThat(cacheService.getEtagForPath("/not_cached/path"), is(etag));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertRawFileToHtml_isCached_andContentNotModified_returnsContentFromCache() {
		given(gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/cached/path";

		stubResponseNotModified();

		String html = getRawFileAsHtml(filePath);

		ArgumentMatcher<HttpEntity> requestEntityEtagHeaderMatch = new ArgumentMatcher<HttpEntity>() {
			@Override
			public boolean matches(Object argument) {
				HttpEntity entity = (HttpEntity) argument;
				return entity.getHeaders().getIfNoneMatch().get(0).equals("a cached etag");
			}
		};

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(), argThat(requestEntityEtagHeaderMatch), (Class<GuideHtml>) anyObject());

		assertThat(html, is(CACHED_CONTENT));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void convertRawFileToHtml_isCached_andContentModified_returnsContentFromResponse_andCachesNewContent() {
		given(gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/cached/path";
		String htmlResponse = "<h1>this is a header</h1>";
		String etag = "\"etagToCache\"";

		stubResponseOK(htmlResponse, etag);

		String html = getRawFileAsHtml(filePath);

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(), (HttpEntity) anyObject(), (Class<GuideHtml>) anyObject());

		assertThat(html, is(htmlResponse));
		assertThat(cacheService.getContentForPath("/cached/path"), is(htmlResponse));
		assertThat(cacheService.getEtagForPath("/cached/path"), is(etag));
	}

	private String getRawFileAsHtml(String filePath) {
		return new GitHubService(gitHub, cacheService).getRawFileAsHtml(filePath);
	}

	private void stubResponseNotModified() {
		ResponseEntity<GuideHtml> entity = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

		Class<GuideHtml> guideHtmlClass = anyObject();
		given(restOperations.exchange(anyString(), (HttpMethod) anyObject(), (HttpEntity) anyObject(), guideHtmlClass))
				.willReturn(entity);
	}

	private void stubResponseOK(String htmlResponse, String etag) {
		GuideHtml response = new GuideHtml(htmlResponse);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("ETag", etag);

		ResponseEntity<GuideHtml> entity = new ResponseEntity<>(response, headers, HttpStatus.OK);

		Class<GuideHtml> guideHtmlClass = anyObject();
		given(restOperations.exchange(anyString(), (HttpMethod) anyObject(), (HttpEntity) anyObject(), guideHtmlClass))
				.willReturn(entity);
	}

}
