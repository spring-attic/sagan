package org.springframework.site.domain.team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.BDDMockito.BDDMyOngoingStubbing;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUserProfile;
import org.springframework.social.github.api.UserOperations;
import org.springframework.web.client.RestOperations;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.*;

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

		given(userOperations.getUserProfile()).willReturn(userProfile);
		given(gitHub.userOperations()).willReturn(userOperations);

		given(teamRepository.findByMemberId(anyString())).willReturn(null);
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
		given(teamRepository.findByMemberId(anyString())).willReturn(new MemberProfile());
		signInService.createMemberProfileIfNeeded("user", gitHub);

		verify(teamRepository, never()).save((MemberProfile) anyObject());
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
		given(gitHub.restOperations()).willReturn(restOperations);
		BDDMyOngoingStubbing<ResponseEntity<Void>> expectedResult = given(restOperations
				.getForEntity(anyString(),
						argThat(new ArgumentMatcher<Class<Void>>() {
							@Override
							public boolean matches(Object argument) {
								return true;
							}
						}), anyString(), anyString()));

		HttpStatus statusCode = isMember ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND;
		expectedResult.willReturn(new ResponseEntity<Void>(statusCode));
	}
}
