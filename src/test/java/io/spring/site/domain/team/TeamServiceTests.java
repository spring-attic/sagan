package io.spring.site.domain.team;

import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.MemberProfileSearchEntryMapper;
import io.spring.site.domain.team.TeamRepository;
import io.spring.site.domain.team.TeamService;
import io.spring.site.search.SearchEntry;
import io.spring.site.search.SearchService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TeamServiceTests {
	@Mock
	private TeamRepository teamRepository;

	@Mock
	private SearchService searchService;

	@Mock
	private MemberProfileSearchEntryMapper mapper;

	private TeamService service;

	@Before
	public void setup() {
		service = new TeamService(teamRepository, searchService, mapper);
	}

	@Test
	public void updateMemberProfileSavesProfileToSearchIndex() {
		MemberProfile savedProfile = new MemberProfile();
		given(teamRepository.findById(1234L)).willReturn(savedProfile);

		SearchEntry searchEntry = new SearchEntry();
		given(mapper.map(savedProfile)).willReturn(searchEntry);
		service.updateMemberProfile(1234L, new MemberProfile());

		verify(searchService).saveToIndex(searchEntry);
	}

	@Test
	public void updateMemberProfileUpdatesAvatarUrlFromGravatarEmail() {
		MemberProfile savedProfile = new MemberProfile();
		given(teamRepository.findById(1234L)).willReturn(savedProfile);

		SearchEntry searchEntry = new SearchEntry();
		given(mapper.map(savedProfile)).willReturn(searchEntry);
		MemberProfile updatedProfile = new MemberProfile();
		updatedProfile.setGravatarEmail("test@example.com");
		service.updateMemberProfile(1234L, updatedProfile);

		assertThat(savedProfile.getGravatarEmail(), equalTo("test@example.com"));
		assertThat(savedProfile.getAvatarUrl(), equalTo("http://gravatar.com/avatar/55502f40dc8b7c769880b10874abc9d0"));
	}

	@Test
	public void updateMemberProfileDoesNotUpdateAvatarUrlIfGravatarEmailIsEmpty() {
		MemberProfile savedProfile = new MemberProfile();
		savedProfile.setAvatarUrl("http://example.com/image.png");
		given(teamRepository.findById(1234L)).willReturn(savedProfile);

		SearchEntry searchEntry = new SearchEntry();
		given(mapper.map(savedProfile)).willReturn(searchEntry);
		MemberProfile updatedProfile = new MemberProfile();
		updatedProfile.setGravatarEmail("");
		service.updateMemberProfile(1234L, updatedProfile);

		assertThat(savedProfile.getAvatarUrl(), equalTo("http://example.com/image.png"));
	}
}
