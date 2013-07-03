package org.springframework.site.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.site.team.SignInService;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GithubAuthenticationSigninAdapterTests {

	private GithubAuthenticationSigninAdapter adapter;
	@SuppressWarnings("unchecked")
	private Connection<GitHub> connection = Mockito.mock(Connection.class);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private SignInService signInService;

	@Before
	public void setup() {
		adapter = new GithubAuthenticationSigninAdapter("/foo", signInService);
	}

	@After
	public void clean() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void signInSunnyDay() {
		when(signInService.isSpringMember(eq("dsyer"), any(GitHub.class))).thenReturn(true);

		adapter.signIn("dsyer", connection, new ServletWebRequest(
				new MockHttpServletRequest()));
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		assertNotNull(authentication);
		assertTrue(authentication.isAuthenticated());
		verify(signInService).createMemberProfileIfNeeded(eq("dsyer"), any(GitHub.class));
	}

	@Test
	public void signInFailure() {
		when(signInService.isSpringMember(eq("dsyer"), any(GitHub.class))).thenReturn(false);

		expectedException.expect(BadCredentialsException.class);
		adapter.signIn("dsyer", connection, new ServletWebRequest(
				new MockHttpServletRequest()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void signInFailureAfterRestException() {
		when(signInService.isSpringMember(eq("dsyer"), any(GitHub.class))).thenThrow(RestClientException.class);
		expectedException.expect(BadCredentialsException.class);
		adapter.signIn("dsyer", connection, new ServletWebRequest(
				new MockHttpServletRequest()));
	}

}