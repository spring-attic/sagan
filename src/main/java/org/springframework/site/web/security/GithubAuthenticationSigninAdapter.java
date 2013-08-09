package org.springframework.site.web.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.SignInService;
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
	public String signIn(String githubId, Connection<?> connection,
			NativeWebRequest request) {
		GitHub gitHub = (GitHub) connection.getApi();
		String githubUsername = connection.getDisplayName();

		try {
			if (!signInService.isSpringMember(githubUsername, gitHub)) {
				throw new BadCredentialsException("User not member of required org");
			}

			MemberProfile member = signInService.getOrCreateMemberProfile(new Long(githubId), gitHub);
			Authentication authentication = new UsernamePasswordAuthenticationToken(
					member.getId(), "N/A",
					AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return path;

		} catch (RestClientException e) {
			throw new BadCredentialsException("User not member of required org");
		}
	}

}