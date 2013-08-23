package org.springframework.site.domain.services;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.site.domain.guides.GuideHtml;
import org.springframework.site.domain.services.github.CachingGitHubRestClient;
import org.springframework.site.domain.services.github.MarkdownHtml;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CachingGitHubRestClientTests {

	private static final String CACHED_CONTENT = "<h1>cached document</h1>";

	private CachingGitHubRestClient client;

	private InMemoryCacheService cacheService = new InMemoryCacheService();

	@Mock
	private GitHub gitHub;

	@Mock
	RestOperations restOperations;

	@Before
	public void setUp() throws Exception {
		this.client = new CachingGitHubRestClient(this.gitHub, this.cacheService);
		this.cacheService.cacheContent("/cached/path", "a cached etag", CACHED_CONTENT);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void sendRequestForHtml_notCached_returnsContentFromGithub_andCaches() {
		given(this.gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/not_cached/path";
		String htmlResponse = "<h1>this is a header</h1>";
		String etag = "\"etagToCache\"";

		stubResponseOK(htmlResponse, etag);

		String html = this.client.sendRequestForHtml(filePath);

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(),
				(HttpEntity<?>) anyObject(), (Class<MarkdownHtml>) anyObject());

		assertThat(html, is(htmlResponse));
		assertThat(this.cacheService.getContentForPath("/not_cached/path"),
				is(htmlResponse));
		assertThat(this.cacheService.getEtagForPath("/not_cached/path"), is(etag));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void sendRequestForHtml_isCached_andContentNotModified_returnsContentFromCache() {
		given(this.gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/cached/path";

		stubResponseNotModified();

		String html = this.client.sendRequestForHtml(filePath);

		ArgumentMatcher<HttpEntity<?>> requestEntityEtagHeaderMatch = new ArgumentMatcher<HttpEntity<?>>() {
			@Override
			public boolean matches(Object argument) {
				HttpEntity<?> entity = (HttpEntity<?>) argument;
				return entity.getHeaders().getIfNoneMatch().get(0)
						.equals("a cached etag");
			}
		};

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(),
				argThat(requestEntityEtagHeaderMatch), (Class<MarkdownHtml>) anyObject());

		assertThat(html, is(CACHED_CONTENT));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void sendRequestForHtml_isCached_andContentModified_returnsContentFromResponse_andCachesNewContent() {
		given(this.gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/cached/path";
		String htmlResponse = "<h1>this is a header</h1>";
		String etag = "\"etagToCache\"";

		stubResponseOK(htmlResponse, etag);

		String html = this.client.sendRequestForHtml(filePath);

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(),
				(HttpEntity<?>) anyObject(), (Class<MarkdownHtml>) anyObject());

		assertThat(html, is(htmlResponse));
		assertThat(this.cacheService.getContentForPath("/cached/path"), is(htmlResponse));
		assertThat(this.cacheService.getEtagForPath("/cached/path"), is(etag));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void sendRequestForJson_notCached_returnsContentFromGithub_andCaches() {
		given(this.gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/not_cached/path";
		String jsonResponse = "{\"foo\": \"bar\"}";
		String etag = "\"etagToCache\"";

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("ETag", etag);

		ResponseEntity<String> entity = new ResponseEntity<>(jsonResponse, headers,
				HttpStatus.OK);

		Class<String> stringClass = anyObject();
		given(
				this.restOperations.exchange(anyString(), (HttpMethod) anyObject(),
						(HttpEntity<?>) anyObject(), stringClass)).willReturn(entity);

		String html = this.client.sendRequestForHtml(filePath);

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(),
				(HttpEntity<?>) anyObject(), (Class<String>) anyObject());

		assertThat(html, is(jsonResponse));
		assertThat(this.cacheService.getContentForPath("/not_cached/path"),
				is(jsonResponse));
		assertThat(this.cacheService.getEtagForPath("/not_cached/path"), is(etag));
	}

	@Test
	public void sendRequestForHtml_withSameUriTemplate_doesNotCauseCacheCollisions() {
		given(this.gitHub.restOperations()).willReturn(this.restOperations);

		String filePath = "/{id}/path";

		String htmlResponse = "<h1>this is a header</h1>";
		String etag = "\"etagToCache\"";

		stubResponseOKWithUriVariable(htmlResponse, etag, "foo");

		String html = this.client.sendRequestForHtml(filePath, "foo");

		assertThat(html, is(htmlResponse));

		String otherHtmlResponse = "<h1>this is a different header</h1>";
		String otherEtag = "\"etagToCache2\"";

		stubResponseOKWithUriVariable(otherHtmlResponse, otherEtag, "bar");

		String otherHtml = this.client.sendRequestForHtml(filePath, "bar");

		verify(this.restOperations).exchange(anyString(), (HttpMethod) anyObject(),
				argThat(new ArgumentMatcher<HttpEntity<?>>() {
					@Override
					public boolean matches(Object argument) {
						HttpEntity<?> entity = (HttpEntity<?>) argument;
						return !entity.getHeaders().containsKey("If-None-Match");
					}

					@Override
					public void describeTo(Description description) {
						description.appendText("expected header without 'If-None-Match'");
					}
				}), Matchers.<Class<MarkdownHtml>> anyObject(), eq("bar"));

		assertThat(otherHtml, is(otherHtmlResponse));

	}

	private void stubResponseNotModified() {
		ResponseEntity<GuideHtml> entity = new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

		Class<GuideHtml> guideHtmlClass = anyObject();
		given(
				this.restOperations.exchange(anyString(), (HttpMethod) anyObject(),
						(HttpEntity<?>) anyObject(), guideHtmlClass)).willReturn(entity);
	}

	private void stubResponseOK(String htmlResponse, String etag) {
		MarkdownHtml response = new MarkdownHtml(htmlResponse);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("ETag", etag);

		ResponseEntity<MarkdownHtml> entity = new ResponseEntity<>(response, headers,
				HttpStatus.OK);

		Class<MarkdownHtml> htmlClass = anyObject();
		given(
				this.restOperations.exchange(anyString(), (HttpMethod) anyObject(),
						(HttpEntity<?>) anyObject(), htmlClass)).willReturn(entity);
	}

	private void stubResponseOKWithUriVariable(String htmlResponse, String etag,
			String uriVariable) {
		MarkdownHtml response = new MarkdownHtml(htmlResponse);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("ETag", etag);

		ResponseEntity<MarkdownHtml> entity = new ResponseEntity<>(response, headers,
				HttpStatus.OK);

		Class<MarkdownHtml> htmlClass = anyObject();
		given(
				this.restOperations.exchange(anyString(), (HttpMethod) anyObject(),
						(HttpEntity<?>) anyObject(), htmlClass, eq(uriVariable)))
				.willReturn(entity);
	}
}
