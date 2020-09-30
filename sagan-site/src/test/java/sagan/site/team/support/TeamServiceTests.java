package sagan.site.team.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import sagan.site.team.MemberProfile;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTests {
	@Mock
	private TeamRepository teamRepository;

	private TeamService service;

	@Before
	public void setup() {
		service = new TeamService(teamRepository);
	}

	@Test
	public void updateMemberProfileUpdatesAvatarUrlFromGravatarEmail() {
		MemberProfile savedProfile = new MemberProfile();
		savedProfile.setName("jlong");
		given(teamRepository.findByUsername("jlong")).willReturn(savedProfile);

		MemberProfile updatedProfile = new MemberProfile();
		updatedProfile.setGravatarEmail("test@example.com");
		service.updateMemberProfile(savedProfile.getName(), updatedProfile);

		assertThat(savedProfile.getGravatarEmail(), equalTo("test@example.com"));
		assertThat(savedProfile.getAvatarUrl(), equalTo("https://gravatar.com/avatar/55502f40dc8b7c769880b10874abc9d0"));
	}

	@Test
	public void updateMemberProfileDoesNotUpdateAvatarUrlIfGravatarEmailIsEmpty() {
		MemberProfile savedProfile = new MemberProfile();
		savedProfile.setName("jlong");
		savedProfile.setAvatarUrl("http://example.com/image.png");
		given(teamRepository.findByUsername("jlong")).willReturn(savedProfile);

		MemberProfile updatedProfile = new MemberProfile();
		updatedProfile.setGravatarEmail("");
		service.updateMemberProfile(savedProfile.getName(), updatedProfile);

		assertThat(savedProfile.getAvatarUrl(), equalTo("http://example.com/image.png"));
	}
}

