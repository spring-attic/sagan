package sagan.guides.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Controller that handles requests from GitHub webhook set up at <a
 * href="https://github.com/spring-guides/">the guides, tutorials and understanding docs
 * repository</a> and clears the rendered docs from cache.
 * <p>
 * This allows to keep the rendered versions of those docs in cache until new changes
 * have been pushed to the repository.
 * <p>
 * Github requests are signed with a shared secret, using an HMAC sha-1 algorithm.
 */
@RestController
@RequestMapping("/webhook/docs/")
class DocsWebhookController {

    private static final Log logger = LogFactory.getLog(DocsWebhookController.class);
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final String PING_EVENT = "ping";

    private final ObjectMapper objectMapper;
    private final Tutorials tutorials;
    private final UnderstandingDocs understandingDocs;
    private final GettingStartedGuides gettingStartedGuides;
    private final Topicals topicals;

    private final Mac hmac;

    @Autowired
    public DocsWebhookController(ObjectMapper objectMapper,
                                 Tutorials tutorials,
                                 UnderstandingDocs understandingDocs,
                                 GettingStartedGuides gettingStartedGuides,
                                 Topicals topicals,
                                 @Value("${WEBHOOK_ACCESS_TOKEN:default}") String accessToken)
            throws NoSuchAlgorithmException, InvalidKeyException {
        this.objectMapper = objectMapper;
        this.tutorials = tutorials;
        this.understandingDocs = understandingDocs;
        this.gettingStartedGuides = gettingStartedGuides;
        this.topicals = topicals;

        // initialize HMAC with SHA1 algorithm and secret
        SecretKeySpec secret = new SecretKeySpec(accessToken.getBytes(CHARSET), HMAC_ALGORITHM);
        hmac = Mac.getInstance(HMAC_ALGORITHM);
        hmac.init(secret);
    }

    @ExceptionHandler(WebhookAuthenticationException.class)
    public ResponseEntity<String> handleWebhookAuthenticationFailure(WebhookAuthenticationException exception) {
        logger.error("Webhook authentication failure: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{ \"message\": \"Forbidden\" }\n");
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handlePayloadParsingException(IOException exception) {
        logger.error("Payload parsing exception", exception);
        return ResponseEntity.badRequest().body("{ \"message\": \"Bad Request\" }\n");
    }

    @RequestMapping(value = "guides", method = POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processGuidesUpdate(@RequestBody String payload,
                                                      @RequestHeader("X-Hub-Signature") String signature,
                                                      @RequestHeader("X-GitHub-Event") String event) throws IOException {

        verifyHmacSignature(payload, signature);
        if (PING_EVENT.equals(event)) {
            return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }\n");
        }
        Map<?, ?> push = this.objectMapper.readValue(payload, Map.class);
        logPayload(push);

        String repositoryName = (String) ((Map<?, ?>) push.get("repository")).get("name");
        String guideName = this.gettingStartedGuides.parseGuideName(repositoryName);
        this.gettingStartedGuides.evictFromCache(guideName);
        return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }\n");
    }

    @RequestMapping(value = "guides/{repositoryName}", method = POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processGuidesUpdate(@RequestBody String payload,
                                                      @RequestHeader("X-Hub-Signature") String signature,
                                                      @RequestHeader("X-GitHub-Event") String event,
                                                      @PathVariable String repositoryName) throws IOException {

        verifyHmacSignature(payload, signature);
        if (PING_EVENT.equals(event)) {
            return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }\n");
        }
        logger.info("Received new webhook payload for push against " + repositoryName);

        String guideName = this.gettingStartedGuides.parseGuideName(repositoryName);
        this.gettingStartedGuides.evictFromCache(guideName);
        return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }\n");
    }

    @RequestMapping(value = "tutorials", method = POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processTutorialsUpdate(@RequestBody String payload,
                                                         @RequestHeader("X-Hub-Signature") String signature,
                                                         @RequestHeader("X-GitHub-Event") String event) throws IOException {

        verifyHmacSignature(payload, signature);
        if (PING_EVENT.equals(event)) {
            return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }\n");
        }
        Map<?, ?> push = this.objectMapper.readValue(payload, Map.class);
        logPayload(push);

        String repositoryName = (String) ((Map<?, ?>) push.get("repository")).get("name");
        String tutorialName = this.tutorials.parseGuideName(repositoryName);
        this.tutorials.evictFromCache(tutorialName);
        return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }\n");
    }

    @RequestMapping(value = "tutorials/{repositoryName}", method = POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processTutorialsUpdate(@RequestBody String payload,
                                                         @RequestHeader("X-Hub-Signature") String signature,
                                                         @RequestHeader("X-GitHub-Event") String event,
                                                         @PathVariable String repositoryName) throws IOException {

        verifyHmacSignature(payload, signature);
        if (PING_EVENT.equals(event)) {
            return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }\n");
        }
        logger.info("Received new webhook payload for push against " + repositoryName);

        String tutorialName = this.tutorials.parseGuideName(repositoryName);
        this.tutorials.evictFromCache(tutorialName);
        return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }\n");
    }

    @RequestMapping(value = "understanding", method = POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processUnderstandingUpdate(@RequestBody String payload,
                                                             @RequestHeader("X-Hub-Signature") String signature,
                                                             @RequestHeader("X-GitHub-Event") String event) throws IOException {

        verifyHmacSignature(payload, signature);
        if (PING_EVENT.equals(event)) {
            return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }\n");
        }
        Map<?, ?> push = this.objectMapper.readValue(payload, Map.class);
        logPayload(push);

        // all understanding docs live under the same repository, so clearing all entries
        this.understandingDocs.clearCache();
        return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }\n");
    }

    @RequestMapping(value = "topicals/{repositoryName}", method = POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> processTopicalsUpdate(@RequestBody String payload,
                                                      @RequestHeader("X-Hub-Signature") String signature,
                                                      @RequestHeader("X-GitHub-Event") String event,
                                                      @PathVariable String repositoryName) throws IOException {

        verifyHmacSignature(payload, signature);
        if (PING_EVENT.equals(event)) {
            return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }\n");
        }
        logger.info("Received new webhook payload for push against " + repositoryName);

        String guideName = this.topicals.parseGuideName(repositoryName);
        this.topicals.evictFromCache(guideName);
        return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }\n");
    }

    protected void verifyHmacSignature(String message, String signature) {
        byte[] sig = hmac.doFinal(message.getBytes(CHARSET));
        String computedSignature = "sha1=" + DatatypeConverter.printHexBinary(sig);
        if (!computedSignature.equalsIgnoreCase(signature)) {
            throw new WebhookAuthenticationException(computedSignature, signature);
        }
    }

    private void logPayload(Map<?, ?> push) {
        if (push.containsKey("head_commit")) {
            final Object headCommit = push.get("head_commit");
            if (headCommit != null) {
                final Map<?, ?> headCommitMap = (Map<?, ?>) headCommit;
                logger.info("Received new webhook payload for push with head_commit message: "
                        + headCommitMap.get("message"));
            }
        } else {
            logger.info("Received new webhook payload for push, but with no head_commit");
        }
    }

}
