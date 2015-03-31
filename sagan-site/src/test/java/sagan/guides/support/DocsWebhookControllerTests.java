package sagan.guides.support;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import java.nio.charset.Charset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link DocsWebhookController}.
 */
public class DocsWebhookControllerTests {

    private ObjectMapper objectMapper;
    @Mock
    private Tutorials tutorials;
    @Mock
    private UnderstandingDocs understandingDocs;
    @Mock
    private GettingStartedGuides gettingStartedGuides;

    private DocsWebhookController controller;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.objectMapper = new ObjectMapper();
        this.controller = new DocsWebhookController(this.objectMapper, this.tutorials, this.understandingDocs,
                this.gettingStartedGuides, "accesstoken");
    }

    @Test
    public void testHmacValue() throws Exception {
        this.controller.verifyHmacSignature("this is a test message", "sha1=5df34e8979dc9a831873a42c6e172546f6937190");
    }

    @Test(expected = WebhookAuthenticationException.class)
    public void testInvalidHmacValue() throws Exception {
        this.controller.verifyHmacSignature("this is a test message", "sha1=wronghmacvalue");
    }

    @Test(expected = JsonParseException.class)
    public void testInvalidPayload() throws Exception {
        String payload = "{ invalid: true";
        this.controller.processGuidesUpdate(payload, "sha1=57a5af868a58183684f68ffe9ff44f112cfbfdaf", "push");
    }

    @Test
    public void testGuideWebhookPing() throws Exception {
        given(this.gettingStartedGuides.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/pingWebhook.json").getInputStream(), Charset.forName("UTF-8"));

        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=7975985CDCA709FF1042DD96A0F3FA0EA7B590B5", "ping");
        assertThat(response.getBody(), is("{ \"message\": \"Successfully processed ping event\" }\n"));
        assertThat(response.getStatusCode().value(), is(200));
        verify(this.gettingStartedGuides, never()).evictFromCache("test-guide");
    }

    @Test
    public void testGuideCacheEviction() throws Exception {
        given(this.gettingStartedGuides.parseGuideName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8"));

        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=372A6699319454075076AE2E9C2821F385682A1B", "push");
        assertThat(response.getBody(), is("{ \"message\": \"Successfully processed update\" }\n"));
        assertThat(response.getStatusCode().value(), is(200));
        verify(this.gettingStartedGuides, times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testTutorialCacheEviction() throws Exception {
        given(this.tutorials.parseTutorialName("gs-test-guide")).willReturn("test-guide");
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8"));

        ResponseEntity response = this.controller.processTutorialsUpdate(payload, "sha1=372A6699319454075076AE2E9C2821F385682A1B", "push");
        assertThat(response.getBody(), is("{ \"message\": \"Successfully processed update\" }\n"));
        assertThat(response.getStatusCode().value(), is(200));
        verify(this.tutorials, times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testUnderstandingCacheEviction() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), Charset.forName("UTF-8"));

        ResponseEntity response = this.controller.processUnderstandingUpdate(payload, "sha1=372A6699319454075076AE2E9C2821F385682A1B", "push");
        assertThat(response.getBody(), is("{ \"message\": \"Successfully processed update\" }\n"));
        assertThat(response.getStatusCode().value(), is(200));
        verify(this.understandingDocs, times(1)).clearCache();
    }

}
