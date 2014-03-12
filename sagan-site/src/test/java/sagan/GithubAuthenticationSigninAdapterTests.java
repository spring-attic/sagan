package sagan;

import sagan.SecurityConfig.GithubAuthenticationSigninAdapter;
import sagan.team.MemberProfile;
import sagan.team.support.SignInService;

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
import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;

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
        MemberProfile newMember = new MemberProfile();
        given(signInService.getOrCreateMemberProfile(anyLong(), any(GitHub.class))).willReturn(newMember);
        given(connection.getDisplayName()).willReturn("dsyer");
        given(signInService.isSpringMember(eq("dsyer"), any(GitHub.class))).willReturn(true);

        adapter.signIn("12345", connection, new ServletWebRequest(new MockHttpServletRequest()));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        verify(signInService).getOrCreateMemberProfile(eq(12345L), any(GitHub.class));
    }

    @Test
    public void signInFailure() {
        given(connection.getDisplayName()).willReturn("dsyer");
        given(signInService.isSpringMember(eq("dsyer"), any(GitHub.class))).willReturn(false);

        expectedException.expect(BadCredentialsException.class);
        adapter.signIn("12345", connection, new ServletWebRequest(new MockHttpServletRequest()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void signInFailureAfterRestException() {
        given(connection.getDisplayName()).willReturn("dsyer");
        given(signInService.isSpringMember(eq("dsyer"), any(GitHub.class))).willThrow(RestClientException.class);
        expectedException.expect(BadCredentialsException.class);
        adapter.signIn("12345", connection, new ServletWebRequest(new MockHttpServletRequest()));
    }

}
