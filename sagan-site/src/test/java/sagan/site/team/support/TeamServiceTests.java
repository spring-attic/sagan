package sagan.site.team.support;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.team.MemberProfile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTests {
	@Mock
	private TeamRepository teamRepository;

	private TeamService service;

	@BeforeEach
	public void setup() {
		service = new TeamService(teamRepository);
	}

	@Test
	public void updateMemberProfileUpdatesAvatarUrlFromGravatarEmail() {
		MemberProfile savedProfile = new MemberProfile();
		savedProfile.setName("jlong");
		given(teamRepository.findByUsername("jlong")).willReturn(Optional.of(savedProfile));

		MemberProfile updatedProfile = new MemberProfile();
		updatedProfile.setGravatarEmail("test@example.com");
		service.updateMemberProfile(savedProfile.getName(), updatedProfile);

		assertThat(savedProfile.getGravatarEmail()).isEqualTo("test@example.com");
		assertThat(savedProfile.getAvatarUrl()).isEqualTo("https://gravatar.com/avatar/55502f40dc8b7c769880b10874abc9d0");
	}

	@Test
	public void updateMemberProfileDoesNotUpdateAvatarUrlIfGravatarEmailIsEmpty() {
		MemberProfile savedProfile = new MemberProfile();
		savedProfile.setName("jlong");
		savedProfile.setAvatarUrl("http://example.com/image.png");
		given(teamRepository.findByUsername("jlong")).willReturn(Optional.of(savedProfile));

		MemberProfile updatedProfile = new MemberProfile();
		updatedProfile.setGravatarEmail("");
		service.updateMemberProfile(savedProfile.getName(), updatedProfile);

		assertThat(savedProfile.getAvatarUrl()).isEqualTo("http://example.com/image.png");
	}
}

