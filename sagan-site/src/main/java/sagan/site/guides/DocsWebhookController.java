package sagan.site.guides;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sagan.site.SiteProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that handles requests from GitHub webhook set up at <a
 * href="https://github.com/spring-guides/">the getting started, tutorial and topical guides
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

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	private static final String HMAC_ALGORITHM = "HmacSHA1";

	private static final String PING_EVENT = "ping";

	private final ObjectMapper objectMapper;

	private final Tutorials tutorials;

	private final GettingStartedGuides gettingStartedGuides;

	private final Topicals topicals;

	private final Mac hmac;

	@Autowired
	public DocsWebhookController(ObjectMapper objectMapper,
			Tutorials tutorials,
			GettingStartedGuides gettingStartedGuides,
			Topicals topicals,
			SiteProperties properties)
			throws NoSuchAlgorithmException, InvalidKeyException {
		this.objectMapper = objectMapper;
		this.tutorials = tutorials;
		this.gettingStartedGuides = gettingStartedGuides;
		this.topicals = topicals;

		// initialize HMAC with SHA1 algorithm and secret
		SecretKeySpec secret = new SecretKeySpec(properties.getGithub().getWebhookToken().getBytes(CHARSET), HMAC_ALGORITHM);
		hmac = Mac.getInstance(HMAC_ALGORITHM);
		hmac.init(secret);
	}

	@ExceptionHandler(WebhookAuthenticationException.class)
	public ResponseEntity<String> handleWebhookAuthenticationFailure(WebhookAuthenticationException exception) {
		logger.error("Webhook authentication failure: " + exception.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{ \"message\": \"Forbidden\" }");
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<String> handlePayloadParsingException(IOException exception) {
		logger.error("Payload parsing exception", exception);
		return ResponseEntity.badRequest().body("{ \"message\": \"Bad Request\" }");
	}

	@PostMapping(path = "guides",
			consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> processGuidesUpdate(@RequestBody String payload,
			@RequestHeader("X-Hub-Signature") String signature,
			@RequestHeader(name = "X-GitHub-Event", required = false, defaultValue = "push") String event) throws IOException {

		verifyHmacSignature(payload, signature);
		if (PING_EVENT.equals(event)) {
			return ResponseEntity.ok("{ \"message\": \"Successfully processed ping event\" }");
		}
		Map<?, ?> push = this.objectMapper.readValue(payload, Map.class);
		logPayload(push);
		String repositoryName = (String) ((Map<?, ?>) push.get("repository")).get("name");
		logger.info("Clearing cache for guide: " + repositoryName);
		evictFromCache(repositoryName);
		return ResponseEntity.ok("{ \"message\": \"Successfully processed update\" }");
	}

	private void verifyHmacSignature(String message, String signature) {
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
		}
		else {
			logger.info("Received new webhook payload for push, but with no head_commit");
		}
	}

	private void evictFromCache(String repositoryName) {
		GuideType guideType = GuideType.fromRepositoryName(repositoryName);
		String guideName = guideType.stripPrefix(repositoryName);
		switch (guideType) {
			case GETTING_STARTED:
				this.gettingStartedGuides.evictFromCache(guideName);
				break;
			case TOPICAL:
				this.topicals.evictFromCache(guideName);
				break;
			case TUTORIAL:
				this.tutorials.evictFromCache(guideName);
				break;
		}
	}

}
