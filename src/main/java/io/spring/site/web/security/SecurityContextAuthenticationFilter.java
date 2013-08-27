package io.spring.site.web.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * Thin filter for Spring Security chain that simply transfers an existing
 * {@link Authentication} from the {@link SecurityContext} if there is one. This
 * is useful when authentication actually happened in a controller, rather than
 * in the filter chain itself.
 * 
 * @author Dave Syer
 * 
 */
public class SecurityContextAuthenticationFilter extends
		AbstractAuthenticationProcessingFilter {

	public SecurityContextAuthenticationFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
		setAuthenticationManager(new AuthenticationManager() {
			// No-op authentication manager is required by base class, but
			// actually redundant here because the authentication has either
			// already happened (happy day) or not (user is not authenticated)
			@Override
			public Authentication authenticate(Authentication authentication)
					throws AuthenticationException {
				throw new IllegalStateException(
						"Unexpected call for AuthenticationManager");
			}
		});
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		return SecurityContextHolder.getContext().getAuthentication();
	}
}