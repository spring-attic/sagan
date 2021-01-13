package sagan.site.support.rewrite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.*;

import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import sagan.site.UrlRewriterFilterConfig;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


import static org.assertj.core.api.Assertions.assertThat;

public class RewriteTests {

	private FilterChain filterChain;

	@BeforeEach
	public void setUp() throws Exception {
		UrlRewriteFilter filter =
				createUrlFilter(UrlRewriterFilterConfig.REWRITE_FILTER_NAME,
						UrlRewriterFilterConfig.REWRITE_FILTER_CONF_PATH);
		filterChain = new PassThroughFilterChain(filter, new MockFilterChain());
	}

	private UrlRewriteFilter createUrlFilter(String filterName, String mappingsFile) throws ServletException {
		UrlRewriteFilter newFilter = new UrlRewriteFilter();
		MockFilterConfig filterConfig = new MockFilterConfig(filterName);
		filterConfig.addInitParameter("confPath", mappingsFile);
		filterConfig.addInitParameter("logLevel", "WARN");
		newFilter.init(filterConfig);
		return newFilter;
	}

	@Test
	void rossensWebSocketPostIsRedirectedToOldBlog() throws Exception {
		validateTemporaryRedirect(
				"https://spring.io/blog/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures",
				"https://assets.spring.io/wp/WebSocketBlogPost.html");
	}

	@Test
	void videosRedirectToYoutube() throws Exception {
		validateTemporaryRedirect("https://spring.io/video", "https://www.youtube.com/springsourcedev");
		validateTemporaryRedirect("https://spring.io/videos", "https://www.youtube.com/springsourcedev");
	}

	@Test
	void supportRenamedMongodbGSGuide() throws Exception {
		validatePermanentRedirect("/guides/gs/accessing-data-mongo", "/guides/gs/accessing-data-mongodb/");
		validatePermanentRedirect("/guides/gs/accessing-data-mongo/", "/guides/gs/accessing-data-mongodb/");
		validateOk("/guides/gs/accessing-data-mongodb/");
	}

	@Test
	void supportRenamedXDGuide() throws Exception {
		validatePermanentRedirect("/guides/gs/spring-xd-osx/", "/guides/gs/spring-xd/");
		validateOk("/guides/gs/spring-xd/");
	}

	@Test
	void gsgGuidesShouldAlwaysHaveTrailingSlash() throws Exception {
		validatePermanentRedirect("/guides/gs/guide-name", "/guides/gs/guide-name/");
		validateOk("/guides/gs/guide-name/");
	}

	@Test
	void tutorialRootShouldHaveTrailingSlash() throws Exception {
		validatePermanentRedirect("/guides/tutorials/guide-name", "/guides/tutorials/guide-name/");
		validateOk("/guides/tutorials/guide-name/");
	}

	@Test
	void tutorialPagesShouldAlwaysHaveTrailingSlash() throws Exception {
		validatePermanentRedirect("/guides/tutorials/guide-name/1", "/guides/tutorials/guide-name/1/");
		validateOk("/guides/tutorials/guide-name/1/");
	}

	@Test
	void tutorialImagesShouldNeverHaveTrailingSlash() throws Exception {
		validateOk("/guides/tutorials/rest/images/yummynoodle.jpg");
	}

	@Test
	void gsgGuidesListingRedirectsToIndex() throws Exception {
		validateTemporaryRedirect("/guides/gs/", "/guides#gs");
		validateTemporaryRedirect("/guides/gs", "/guides#gs");
	}

	@Test
	void gsgTutorialsListingRedirectsToIndex() throws Exception {
		validateTemporaryRedirect("/guides/tutorials/", "/guides#tutorials");
		validateTemporaryRedirect("/guides/tutorials", "/guides#tutorials");
	}

	@Test
	void stripsWwwSubdomain() throws Exception {
		validatePermanentRedirect("https://www.spring.io", "https://spring.io/");
		validatePermanentRedirect("https://www.spring.io/something", "https://spring.io/something");
	}

	@Test
	void projectPageIndexIsNotRedirected() throws Exception {
		validateOk("https://spring.io/projects");
	}

	@Test
	void projectPageIndexWithSlashIsNotRedirected() throws Exception {
		validateOk("https://spring.io/projects/");
	}

	@Test
	void projectPagesAreRedirected() throws Exception {
		validateTemporaryRedirect("https://spring.io/spring-data", "https://spring.io/projects/spring-data");
	}

	@Test
	void formerDocsPagesAreRedirected() throws Exception {
		validateTemporaryRedirect("https://spring.io/docs", "https://spring.io/projects");
	}

	@Test
	void linkedinIsRedirected() throws Exception {
		validateTemporaryRedirect("https://spring.io/linkedin", "https://www.linkedin.com/groups/46964");
	}

	@Test
	void deprecatedTutorialsRedirected() throws Exception {
		validateTemporaryRedirect("https://spring.io/guides/tutorials/data/", "/guides");
		validateTemporaryRedirect("https://spring.io/guides/tutorials/web/", "/guides");
	}

	@Test
	void tools3IsRedirected() throws Exception {
		validateTemporaryRedirect("https://spring.io/tools3", "/tools#suite-three");
		validateTemporaryRedirect("https://spring.io/tools3/sts/all", "/tools#suite-three");
	}

	@Test
	void deprecatedWarGuideRedirected() throws Exception {
		validateTemporaryRedirect("https://spring.io/guides/gs/convert-jar-to-war-maven/", "/guides/gs/convert-jar-to-war/");
	}

	@Test
	void tanzuSecurityPolicy() throws Exception {
		validateTemporaryRedirect("https://spring.io/security-policy", "https://tanzu.vmware.com/security");
	}

	private void validateTemporaryRedirect(String requestedUrl, String redirectedUrl) throws IOException,
			ServletException, URISyntaxException {
		validateRedirect(requestedUrl, redirectedUrl, 302);
	}

	private void validatePermanentRedirect(String requestedUrl, String redirectedUrl) throws IOException,
			ServletException, URISyntaxException {
		validateRedirect(requestedUrl, redirectedUrl, 301);
	}

	private void validateRedirect(String requestedUrl, String redirectedUrl, int expectedStatus) throws IOException,
			ServletException, URISyntaxException {
		MockHttpServletRequest servletRequest = new MockHttpServletRequest();
		URI requestUrl = new URI(requestedUrl);
		if (requestUrl.getHost() != null) {
			servletRequest.addHeader("host", requestUrl.getHost());
		}
		servletRequest.setRequestURI(requestUrl.getPath());
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		filterChain.doFilter(servletRequest, servletResponse);

		assertThat(servletResponse.getRedirectedUrl()).isEqualTo(redirectedUrl);
		assertThat(servletResponse.getStatus()).as("Incorrect status code for " + redirectedUrl).isEqualTo(expectedStatus);
	}

	private void validateOk(String requestedUrl) throws IOException, ServletException, URISyntaxException {
		MockHttpServletRequest servletRequest = new MockHttpServletRequest();
		URI requestUrl = new URI(requestedUrl);
		if (requestUrl.getHost() != null) {
			servletRequest.addHeader("host", requestUrl.getHost());
		}
		servletRequest.setRequestURI(requestUrl.getPath());
		MockHttpServletResponse servletResponse = new MockHttpServletResponse();

		filterChain.doFilter(servletRequest, servletResponse);

		assertThat(servletResponse.getStatus()).isEqualTo(HttpStatus.OK.value());
	}
}
