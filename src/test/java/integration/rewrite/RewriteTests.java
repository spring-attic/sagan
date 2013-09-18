package integration.rewrite;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.PassThroughFilterChain;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RewriteTests {

    private FilterChain filterChain;

    @Before
    public void setUp() throws Exception {
        UrlRewriteFilter filter = createUrlFilter("rewriteFilter", "urlrewrite/urlrewrite.xml");
        UrlRewriteFilter mappingsFilter = createUrlFilter("mappingsRewriteFilter", "urlrewrite/urlrewrite-generated.xml");
        filterChain = new PassThroughFilterChain(mappingsFilter, new PassThroughFilterChain(filter, new MockFilterChain()));
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
    public void legacySiteDocrootsAreRedirected() throws Exception {
        validatePermanentRedirect("http://springsource.org", "http://spring.io");
        validatePermanentRedirect("http://www.springsource.org", "http://spring.io");
        validatePermanentRedirect("http://springsource.org/", "http://spring.io");
        validatePermanentRedirect("http://www.springsource.org/", "http://spring.io");
        validatePermanentRedirect("http://springframework.org", "http://spring.io");
        validatePermanentRedirect("http://www.springframework.org", "http://spring.io");
        validatePermanentRedirect("http://springframework.org/", "http://spring.io");
        validatePermanentRedirect("http://www.springframework.org/", "http://spring.io");
    }

    @Test
    public void interface21Redirects() throws Exception {
        validatePermanentRedirect("http://interface21.com", "http://spring.io");
        validatePermanentRedirect("http://www.interface21.com", "http://spring.io");
        validatePermanentRedirect("http://blog.interface21.com", "http://spring.io/blog");
    }

    @Test
    public void legacyStsGgtsRedirects() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/sts/welcome", "http://spring.io/tools/sts/welcome");
        validatePermanentRedirect("http://www.springsource.org/groovy-grails-tool-suite-download", "http://spring.io/tools/ggts");
        validateTemporaryRedirect("http://www.springsource.org/ggts/welcome", "http://grails.org/products/ggts");
        validatePermanentRedirect("http://www.springsource.org/springsource-tool-suite-download", "http://spring.io/tools/sts");
        validatePermanentRedirect("http://www.springsource.org/downloads/sts-ggts", "http://spring.io/tools");
    }

    @Test
    public void legacyBlogSiteRequestsAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org", "http://spring.io/blog/");
        validatePermanentRedirect("http://blog.springsource.org/", "http://spring.io/blog/");
        validatePermanentRedirect("http://blog.springsource.com", "http://spring.io/blog/");
        validatePermanentRedirect("http://blog.springsource.com/", "http://spring.io/blog/");
    }

    @Test
    public void legacyBlogFeedRequestsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://spring.io/blog/feed", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/feed/", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/feed/atom/", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/category/security/feed/", "http://spring.io/blog.atom");
        validateTemporaryRedirect("http://spring.io/blog/main/feed/", "http://spring.io/blog.atom");
    }

    @Test
    public void legacyBlogResourceRequestsAreRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.org/wp-content/plugins/syntaxhighlighter/syntaxhighlighter2/scripts/shBrushJava.js",
                "http://wp.spring.io/wp-content/plugins/syntaxhighlighter/syntaxhighlighter2/scripts/shBrushJava.js");
        validateTemporaryRedirect("http://wp.spring.io/blog/wp-content/plugins/syntaxhighlighter/syntaxhighlighter2/scripts/shBrushJava.js",
                "http://wp.spring.io/wp-content/plugins/syntaxhighlighter/syntaxhighlighter2/scripts/shBrushJava.js");
    }

    @Test
    public void rossensWebSocketPostIsRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.org/blog/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures",
                                  "http://wp.spring.io/2013/07/24/spring-framework-4-0-m2-websocket-messaging-architectures");
    }

    @Test
    public void legacySTSBlogFeedRequestsAreRedirectedToOldBlog() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.com/main/feed/", "http://wp.spring.io/main/feed/");
    }

    @Test
    public void blogPagesAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org/anything", "http://spring.io/blog/anything");
        validatePermanentRedirect("http://blog.springsource.org/anything", "http://spring.io/blog/anything");
    }

    @Test
    public void blogAssetsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://blog.springsource.org/wp-content/uploads/attachment.zip", "http://wp.spring.io/wp-content/uploads/attachment.zip");
    }

    @Test
    public void blogAuthorsAreRedirected() throws Exception {
        validatePermanentRedirect("http://blog.springsource.org/author/cbeams", "http://spring.io/team/cbeams");
    }

    @Test
    public void drupalNodesAreRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/node/3762", "http://spring.io/blog/2012/12/10/spring-framework-3-2-ga-released");
        validatePermanentRedirect("http://www.springsource.org/BusinessIntelligenceWithSpringAndBIRT", "http://spring.io/blog/2012/01/30/spring-framework-birt");
    }

    @Test
    public void oldCaseStudiesAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/files/uploads/file.pdf", "http://drupal.spring.io/files/uploads/file.pdf");
    }

    @Test
    public void videosRedirectToYoutube() throws ServletException, IOException, URISyntaxException {
        validateTemporaryRedirect("http://spring.io/video", "http://www.youtube.com/springsourcedev");
        validateTemporaryRedirect("http://spring.io/videos", "http://www.youtube.com/springsourcedev");
        validateTemporaryRedirect("http://www.springsource.org/videos", "http://www.youtube.com/springsourcedev");
        validateTemporaryRedirect("http://www.example.com/videos", "http://www.youtube.com/springsourcedev");
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
        validateTemporaryRedirect("http://springsource.org/spring-amqp", "http://projects.spring.io/spring-amqp");
        validatePermanentRedirect("http://www.springsource.org/spring-amqp", "http://projects.spring.io/spring-amqp");
    }

    @Test
    public void legacySchemaRequestsAreRedirected() throws Exception {
        for (String host : new String [] { "springsource.org", "www.springsource.org",
                                           "springframework.org", "www.springframework.org"}) {
            validatePermanentRedirect("http://" + host + "/schema", "http://schema.spring.io");
            validatePermanentRedirect("http://" + host + "/schema/", "http://schema.spring.io/");
            validatePermanentRedirect("http://" + host + "/schema/oxm", "http://schema.spring.io/oxm");
            validatePermanentRedirect("http://" + host + "/schema/oxm/spring-oxm.xsd",
                                      "http://schema.spring.io/oxm/spring-oxm.xsd");
        }
    }

    @Test
    public void legacyStaticDocsRequestsAreRedirected() throws Exception {
        for (String host : new String [] { "static.springsource.org", "static.springframework.org" }) {
            validateTemporaryRedirect("http://" + host + "", "http://spring.io/docs");
            validateTemporaryRedirect("http://" + host + "/", "http://spring.io/docs");
            validatePermanentRedirect("http://" + host + "/spring-anything", "http://docs.spring.io/spring-anything");
        }
    }

    @Test
    public void legacyForumRequestsAreRedirected() throws Exception {
        validatePermanentRedirect("http://forum.springsource.org", "http://forum.spring.io/");
        validatePermanentRedirect("http://forum.springframework.org", "http://forum.spring.io/");
        // something is stripping the query strings during testing, but this actually
        // works against the running site
        // validatePermanentRedirect("http://forum.springsource.org/showthread.php?48738-Getting-Spring-to-throw-duplicate-bean-definition-exception",
        //                           "http://forum.spring.io/showthread.php?48738-Getting-Spring-to-throw-duplicate-bean-definition-exception");
        validatePermanentRedirect("http://forum.springsource.org/showthread.php?48738-Getting-Spring-to-throw-duplicate-bean-definition-exception",
                                  "http://forum.spring.io/showthread.php");
    }

    @Test
    public void inboundLinksFromEclipseMarketplaceAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/eclipse-downloads", "http://spring.io/tools/eclipse");
        validateTemporaryRedirect("http://www.springsource.com/products/eclipse-downloads", "http://spring.io/tools/eclipse");
    }

    @Test
    public void legacyLinkedInPathIsRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/linkedin", "http://www.linkedin.com/groups/Spring-Users-46964?gid=46964");
    }

    @Test
    public void legacyDownloadRequestsAreRedirectedToProjectsPage() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/download", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/download/community", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/spring-community-download", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/springsource-community-download", "http://spring.io/projects");
        validateTemporaryRedirect("http://www.springsource.org/spring-community-download?utm_source=eclipse.org&utm_medium=web&utm_content=promotedDL&utm_campaign=STS",
                                  "http://spring.io/projects");
    }

    @Test
    public void ldapIsRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/ldap", "http://projects.spring.io/spring-ldap");
    }

    @Test
    public void gemfireIsRedirected() throws Exception {
        validatePermanentRedirect("http://www.springsource.org/spring-gemfire", "http://projects.spring.io/spring-data-gemfire");
    }

    @Test
    public void rooIsRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/roo", "https://github.com/spring-projects/spring-roo");
        validateTemporaryRedirect("http://www.springsource.org/spring-roo", "https://github.com/spring-projects/spring-roo");
    }

    @Test
    public void legacyNewsAndEventsFeedRequestsAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/node/feed", "http://drupal.spring.io/node/feed");
        validateTemporaryRedirect("http://www.springsource.org/newsblog/feed", "http://drupal.spring.io/newsblog/feed");
    }

    @Test
    public void legacyDrupalFilesAreRedirected() throws Exception {
        validateTemporaryRedirect("http://www.springsource.org/files/SpringOne/2013/training-bg-image.png", "http://drupal.spring.io/files/SpringOne/2013/training-bg-image.png");
        validateTemporaryRedirect("http://www.springsource.org/files/other.jpg", "http://drupal.spring.io/files/other.jpg");
    }

    private void validateTemporaryRedirect(String requestedUrl, String redirectedUrl) throws IOException, ServletException, URISyntaxException {
        validateRedirect(requestedUrl, redirectedUrl, 302);
    }

    private void validatePermanentRedirect(String requestedUrl, String redirectedUrl) throws IOException, ServletException, URISyntaxException {
        validateRedirect(requestedUrl, redirectedUrl, 301);
    }

    private void validateRedirect(String requestedUrl, String redirectedUrl, int expectedStatus) throws IOException, ServletException, URISyntaxException {
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
