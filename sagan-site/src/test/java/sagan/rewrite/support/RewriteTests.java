package sagan.rewrite.support;

import sagan.SiteConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Test;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.PassThroughFilterChain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RewriteTests {

    private FilterChain filterChain;

    @Before
    public void setUp() throws Exception {
        UrlRewriteFilter filter =
                createUrlFilter(SiteConfig.REWRITE_FILTER_NAME,
                        SiteConfig.REWRITE_FILTER_CONF_PATH);
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
    public void rossensWebSocketPostIsRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect(
                "http://spring.io/blog/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures",
                "http://assets.spring.io/wp/WebSocketBlogPost.html");
    }

    @Test
    public void videosRedirectToYoutube() throws ServletException, IOException, URISyntaxException {
        validateTemporaryRedirect("http://spring.io/video", "http://www.youtube.com/springsourcedev");
        validateTemporaryRedirect("http://spring.io/videos", "http://www.youtube.com/springsourcedev");
    }

    @Test
    public void supportRenamedMongodbGSGuide() throws ServletException, IOException, URISyntaxException {
        validatePermanentRedirect("/guides/gs/accessing-data-mongo", "/guides/gs/accessing-data-mongodb/");
        validatePermanentRedirect("/guides/gs/accessing-data-mongo/", "/guides/gs/accessing-data-mongodb/");
        validateOk("/guides/gs/accessing-data-mongodb/");
    }

    @Test
    public void gsgGuidesShouldAlwaysHaveTrailingSlash() throws ServletException, IOException, URISyntaxException {
        validatePermanentRedirect("/guides/gs/guide-name", "/guides/gs/guide-name/");
        validateOk("/guides/gs/guide-name/");
    }

    @Test
    public void tutorialRootShouldHaveTrailingSlash() throws ServletException, IOException, URISyntaxException {
        validatePermanentRedirect("/guides/tutorials/guide-name", "/guides/tutorials/guide-name/");
        validateOk("/guides/tutorials/guide-name/");
    }

    @Test
    public void tutorialPagesShouldAlwaysHaveTrailingSlash() throws ServletException, IOException, URISyntaxException {
        validatePermanentRedirect("/guides/tutorials/guide-name/1", "/guides/tutorials/guide-name/1/");
        validateOk("/guides/tutorials/guide-name/1/");
    }

    @Test
    public void tutorialImagesShouldNeverHaveTrailingSlash() throws ServletException, IOException, URISyntaxException {
        validateOk("/guides/tutorials/rest/images/yummynoodle.jpg");
    }

    @Test
    public void gsgGuidesListingRedirectsToIndex() throws ServletException, IOException, URISyntaxException {
        validateTemporaryRedirect("/guides/gs/", "/guides#gs");
        validateTemporaryRedirect("/guides/gs", "/guides#gs");
    }

    @Test
    public void gsgTutorialsListingRedirectsToIndex() throws ServletException, IOException, URISyntaxException {
        validateTemporaryRedirect("/guides/tutorials/", "/guides#tutorials");
        validateTemporaryRedirect("/guides/tutorials", "/guides#tutorials");
    }

    @Test
    public void stripsWwwSubdomain() throws ServletException, IOException, URISyntaxException {
        validatePermanentRedirect("http://www.spring.io", "http://spring.io/");
        validatePermanentRedirect("http://www.spring.io/something", "http://spring.io/something");
    }

    @Test
    public void projectPageIndexIsNotRedirected() throws ServletException, IOException, URISyntaxException {
        validateOk("http://spring.io/projects");
    }

    @Test
    public void projectPageIndexWithSlashIsNotRedirected() throws ServletException, IOException, URISyntaxException {
        validateOk("http://spring.io/projects/");
    }

    @Test
    public void projectPagesAreRedirected() throws ServletException, IOException, URISyntaxException {
        validateTemporaryRedirect("http://spring.io/projects/spring-data", "http://projects.spring.io/spring-data");
        validateTemporaryRedirect("http://spring.io/spring-data", "http://projects.spring.io/spring-data");
    }

    @Test
    public void ggtsWelcomePageIsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/tools/ggts/welcome", "http://grails.org/products/ggts/welcome");
    }

    @Test
    public void gplusIsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/gplus", "https://plus.google.com/+springframework/");
    }

    @Test
    public void linkedinIsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/linkedin", "http://www.linkedin.com/groups/Spring-Users-46964");
    }

    @Test
    public void deprecatedTutorialsRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/guides/tutorials/rest/","/guides");
        validateTemporaryRedirect("http://spring.io/guides/tutorials/data/","/guides");
        validateTemporaryRedirect("http://spring.io/guides/tutorials/web/","/guides");
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

        assertThat(servletResponse.getRedirectedUrl(), equalTo(redirectedUrl));
        assertThat("Incorrect status code for " + redirectedUrl, servletResponse.getStatus(), equalTo(expectedStatus));
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

        assertThat(servletResponse.getStatus(), equalTo(HttpStatus.OK.value()));
    }
}
