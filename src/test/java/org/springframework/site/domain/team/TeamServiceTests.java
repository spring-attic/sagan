package org.springframework.site.domain.team;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;

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
		given(teamRepository.findByMemberId("memberid")).willReturn(savedProfile);

		SearchEntry searchEntry = new SearchEntry();
		given(mapper.map(savedProfile)).willReturn(searchEntry);
		service.updateMemberProfile("memberid", new MemberProfile());

		verify(searchService).saveToIndex(searchEntry);
	}
}
