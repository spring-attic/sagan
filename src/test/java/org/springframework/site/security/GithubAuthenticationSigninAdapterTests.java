package org.springframework.site.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.web.context.request.ServletWebRequest;

public class GithubAuthenticationSigninAdapterTests {
	
	private GithubAuthenticationSigninAdapter adapter = new GithubAuthenticationSigninAdapter("/foo");
	private Connection<?> connection = Mockito.mock(Connection.class);
	
	@After
	public void clean() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void signInSunnyDay() {
		adapter.signIn("dsyer", connection , new ServletWebRequest(new MockHttpServletRequest()));
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		assertNotNull(authentication);
		assertTrue(authentication.isAuthenticated());
	}

}