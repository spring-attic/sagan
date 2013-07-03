package org.springframework.site.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.site.team.SignInService;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.NativeWebRequest;

public class GithubAuthenticationSigninAdapter implements SignInAdapter {


	private String path;
	private final SignInService signInService;

	public GithubAuthenticationSigninAdapter(String path, SignInService signInService) {
		this.path = path;
		this.signInService = signInService;
	}

	@Override
	public String signIn(String userId, Connection<?> connection,
			NativeWebRequest request) {

		GitHub gitHub = (GitHub) connection.getApi();
		try {
			if (!signInService.isSpringMember(userId, gitHub)) {
				throw new BadCredentialsException("User not member of required org");
			}
			signInService.createMemberProfileIfNeeded(userId, gitHub);
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