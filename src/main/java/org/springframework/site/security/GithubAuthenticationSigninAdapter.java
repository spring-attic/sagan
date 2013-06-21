package org.springframework.site.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.NativeWebRequest;

public class GithubAuthenticationSigninAdapter implements SignInAdapter {

	private static final String IS_MEMBER_PATH = "https://api.github.com/teams/{team}/members/{user}";

	private String path;

	public GithubAuthenticationSigninAdapter(String path) {
		this.path = path;
	}

	@Override
	public String signIn(String userId, Connection<?> connection,
			NativeWebRequest request) {

		GitHub template = (GitHub) connection.getApi();
		try {
			ResponseEntity<Void> response = template.restOperations().getForEntity(IS_MEMBER_PATH, Void.class, "435080", userId);
			if (response.getStatusCode()!=HttpStatus.NO_CONTENT) {
				throw new BadCredentialsException("User not member of required org");
			}
		} catch (RestClientException e) {
			throw new BadCredentialsException("User not member of required org");
		}

		Authentication authentication = new UsernamePasswordAuthenticationToken(
				userId, "N/A",
				AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
		// Populate Spring SecurityContext (i.e. assume authentication complete
		// at this point)
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return path;

	}

}