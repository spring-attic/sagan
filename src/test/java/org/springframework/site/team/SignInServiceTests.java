package org.springframework.site.team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.UserOperations;
import org.springframework.web.client.RestOperations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignInServiceTests {

	@Mock
	private GitHub gitHub;

	@Mock
	private TeamRepository teamRepository;

	private SignInService signInService;
	private String username = "user";
	private String name = "Full Name";
	private String location = "London";
	private String email = "user@example.com";
	private String avatarUrl = "http://gravatar.com/avatar/ABC";

	@Before
	public void setup() {
		signInService = new SignInService(teamRepository);
	}

	@Test
	public void createAMemberProfileIfOneDoesNotExist() {
		GitHubUserProfile userProfile = new GitHubUserProfile(1L, username, name, location, "", "", email, avatarUrl, null);
		UserOperations userOperations = mock(UserOperations.class);

		when(userOperations.getUserProfile()).thenReturn(userProfile);
		when(gitHub.userOperations()).thenReturn(userOperations);

		when(teamRepository.findByMemberId(anyString())).thenReturn(null);
		signInService.createMemberProfileIfNeeded("user", gitHub);

		verify(teamRepository).save(argThat(new ArgumentMatcher<MemberProfile>() {
			@Override
			public boolean matches(Object argument) {
				MemberProfile profile = (MemberProfile)argument;
				return profile != null &&
						username.equals(profile.getMemberId()) &&
						name.equals(profile.getName()) &&
						username.equals(profile.getGithubUsername()) &&
						avatarUrl.equals(profile.getAvatarUrl());
			}
		}));
	}

	@Test
	public void doNotCreateAMemberProfileIfOneDoesExist() {
		when(teamRepository.findByMemberId(anyString())).thenReturn(new MemberProfile());
		signInService.createMemberProfileIfNeeded("user", gitHub);

		verify(teamRepository, never()).save(any(MemberProfile.class));
	}

	@Test
	public void isSpringMember() {
		mockIsMemberOfTeam(true);

		assertThat(signInService.isSpringMember("member", gitHub), is(true));
	}

	@Test
	public void isNotSpringMember() {
		mockIsMemberOfTeam(false);

		assertThat(signInService.isSpringMember("notmember", gitHub), is(false));
	}

	private void mockIsMemberOfTeam(boolean isMember) {
		RestOperations restOperations = mock(RestOperations.class);
		when(gitHub.restOperations()).thenReturn(restOperations);
		OngoingStubbing<ResponseEntity<Void>> expectedResult = when(restOperations
				.getForEntity(anyString(),
						argThat(new ArgumentMatcher<Class<Void>>() {
							@Override
							public boolean matches(Object argument) {
								return true;
							}
						}), anyString(), anyString()));

		HttpStatus statusCode = isMember ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND;
		expectedResult.thenReturn(new ResponseEntity<Void>(statusCode));
	}
}
