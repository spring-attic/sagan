package sagan.site.guides;


import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link DocsWebhookController}.
 */
@ExtendWith(MockitoExtension.class)
public class DocsWebhookControllerTests {

    private ObjectMapper objectMapper;
    @Mock
    private Tutorials tutorials;
    @Mock
    private GettingStartedGuides gettingStartedGuides;
    @Mock
    private Topicals topicals;

    private DocsWebhookController controller;

    @BeforeEach
    public void setup() throws Exception {
        this.objectMapper = new ObjectMapper();
        this.controller = new DocsWebhookController(this.objectMapper, this.tutorials,
                this.gettingStartedGuides, this.topicals, "accesstoken");
    }

    @Test
    public void testHmacValue() throws Exception {
        this.controller.verifyHmacSignature("this is a test message", "sha1=5df34e8979dc9a831873a42c6e172546f6937190");
    }

    @Test
    public void testInvalidHmacValue() throws Exception {
        assertThatThrownBy(() -> this.controller.verifyHmacSignature("this is a test message", "sha1=wronghmacvalue"))
		.isInstanceOf(WebhookAuthenticationException.class);
    }

    @Test
    public void testInvalidPayload() throws Exception {
        String payload = "{ invalid: true";
		assertThatThrownBy(() -> this.controller.processGuidesUpdate(payload, "sha1=57a5af868a58183684f68ffe9ff44f112cfbfdaf", "push"))
				.isInstanceOf(JsonParseException.class);
    }

    @Test
    public void testGuideWebhookPing() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/pingWebhook.json").getInputStream(), StandardCharsets.UTF_8)
				.replaceAll("[\\n|\\r]","");;

        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=9E629DCCF4472F600D048510354BE400B8EB25CB", "ping");
        assertThat(response.getBody()).isEqualTo("{ \"message\": \"Successfully processed ping event\" }\n");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(this.gettingStartedGuides, never()).evictFromCache("test-guide");
    }

    @Test
    public void testGuideCacheEviction() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), StandardCharsets.UTF_8)
				.replaceAll("[\\n|\\r]","");;

        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push");
        assertThat(response.getBody()).isEqualTo("{ \"message\": \"Successfully processed update\" }\n");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(this.gettingStartedGuides, times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testGuideCacheEviction2() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), StandardCharsets.UTF_8)
				.replaceAll("[\\n|\\r]","");;

        ResponseEntity response = this.controller.processGuidesUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push",
                "gs-test-guide");
        assertThat(response.getBody()).isEqualTo("{ \"message\": \"Successfully processed update\" }\n");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(this.gettingStartedGuides, times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testTutorialCacheEviction() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), StandardCharsets.UTF_8)
				.replaceAll("[\\n|\\r]","");

        ResponseEntity response = this.controller.processTutorialsUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push");
        assertThat(response.getBody()).isEqualTo("{ \"message\": \"Successfully processed update\" }\n");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(this.tutorials, times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testTutorialCacheEviction2() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), StandardCharsets.UTF_8)
				.replaceAll("[\\n|\\r]","");;

        ResponseEntity response = this.controller.processTutorialsUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98", "push",
                "gs-test-guide");
        assertThat(response.getBody()).isEqualTo("{ \"message\": \"Successfully processed update\" }\n");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(this.tutorials, times(1)).evictFromCache("test-guide");
    }

    @Test
    public void testTopicalCacheEviction() throws Exception {
        String payload = StreamUtils.copyToString(
                new ClassPathResource("fixtures/webhooks/docsWebhook.json").getInputStream(), StandardCharsets.UTF_8)
                .replaceAll("[\\n|\\r]","");;

        ResponseEntity response = this.controller.processTopicalsUpdate(payload, "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98",
                "push", "top-test-guide");
        assertThat(response.getBody()).isEqualTo("{ \"message\": \"Successfully processed update\" }\n");
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(this.topicals, times(1)).evictFromCache("test-guide");
    }

}
