package org.springframework.site.security;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.context.request.ServletWebRequest;

public class GithubAuthenticationSigninAdapterTests {

	private GithubAuthenticationSigninAdapter adapter = new GithubAuthenticationSigninAdapter(
			"/foo");
	@SuppressWarnings("unchecked")
	private Connection<GitHub> connection = Mockito.mock(Connection.class);

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@After
	public void clean() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void signInSunnyDay() {
		mockIsMemberOfTeam().thenReturn(
				new ResponseEntity<Void>(HttpStatus.NO_CONTENT));

		adapter.signIn("dsyer", connection, new ServletWebRequest(
				new MockHttpServletRequest()));
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		assertNotNull(authentication);
		assertTrue(authentication.isAuthenticated());
	}

	@Test
	public void signInFailure() {
		mockIsMemberOfTeam().thenReturn(
				new ResponseEntity<Void>(HttpStatus.NOT_FOUND));

		expectedException.expect(BadCredentialsException.class);
		adapter.signIn("dsyer", connection, new ServletWebRequest(
				new MockHttpServletRequest()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void signInFailureAfterRestException() {
		mockIsMemberOfTeam().thenThrow(RestClientException.class);
		expectedException.expect(BadCredentialsException.class);
		adapter.signIn("dsyer", connection, new ServletWebRequest(
				new MockHttpServletRequest()));
	}

	private OngoingStubbing<ResponseEntity<Void>> mockIsMemberOfTeam() {
		GitHub github = mock(GitHub.class);
		when(connection.getApi()).thenReturn(github);
		RestOperations restOperations = mock(RestOperations.class);
		when(github.restOperations()).thenReturn(restOperations);
		OngoingStubbing<ResponseEntity<Void>> expectedResult = when(restOperations
				.getForEntity(anyString(),
						argThat(new ArgumentMatcher<Class<Void>>() {
							@Override
							public boolean matches(Object argument) {
								return true;
							}
						}), anyString(), anyString()));
		return expectedResult;
	}

}