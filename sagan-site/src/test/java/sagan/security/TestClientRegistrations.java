package sagan.security;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * @author Rob Winch
 */
public class TestClientRegistrations {
	public static ClientRegistration.Builder clientRegistration() {
		return ClientRegistration.withRegistrationId("registration-id")
				.redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
				.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.scope("read:user")
				.authorizationUri("https://example.com/login/oauth/authorize")
				.tokenUri("https://example.com/login/oauth/access_token")
				.jwkSetUri("https://example.com/oauth2/jwk")
				.userInfoUri("https://example.com/user")
				.userNameAttributeName("id")
				.clientName("Client Name")
				.clientId("client-id")
				.clientSecret("client-secret");
	}
}
