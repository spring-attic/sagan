package org.springframework.site.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.SignInAdapter;
import org.springframework.web.context.request.NativeWebRequest;

public class GithubAuthenticationSigninAdapter implements SignInAdapter {

	private String path;

	public GithubAuthenticationSigninAdapter(String path) {
		this.path = path;
	}

	@Override
	public String signIn(String userId, Connection<?> connection,
			NativeWebRequest request) {

		// TODO: get group info from github and determine role
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				userId, "N/A",
				AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
		// Populate Spring SecurityContext (i.e. assume authentication complete
		// at this point)
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return path;

	}

}