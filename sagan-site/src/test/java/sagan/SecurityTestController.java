package sagan;

import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sagan.security.GithubMemberOAuth2UserService;

/**
 * @author Rob Winch
 */
@RestController
@TestConfiguration
@Import({ OAuth2ClientAutoConfiguration.class, GithubMemberOAuth2UserService.class })
class SecurityTestController {
	@RequestMapping("/**")
	String ok() {
		return "ok";
	}
}
