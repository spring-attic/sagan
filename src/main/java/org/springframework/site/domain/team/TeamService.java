package org.springframework.site.domain.team;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
	private final TeamRepository teamRepository;
	private final SearchService searchService;
	private final MemberProfileSearchEntryMapper mapper;

	@Autowired
	public TeamService(TeamRepository teamRepository, SearchService searchService, MemberProfileSearchEntryMapper mapper) {
		this.teamRepository = teamRepository;
		this.searchService = searchService;
		this.mapper = mapper;
	}

	public MemberProfile fetchMemberProfile(String memberId) {
		return teamRepository.findByMemberId(memberId);
	}

	public void updateMemberProfile(String memberId, MemberProfile profile) {
		MemberProfile existingProfile = fetchMemberProfile(memberId);
		existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
		existingProfile.setTwitterUsername(profile.getTwitterUsername());
		existingProfile.setBio(profile.getBio());
		existingProfile.setName(profile.getName());
		existingProfile.setGithubUsername(profile.getGithubUsername());
		existingProfile.setTwitterUsername(profile.getTwitterUsername());
		existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
		existingProfile.setLanyrdUsername(profile.getLanyrdUsername());
		existingProfile.setLocation(profile.getLocation());
		existingProfile.setGeoLocation(profile.getGeoLocation());
		existingProfile.setVideoEmbeds(profile.getVideoEmbeds());
		teamRepository.save(existingProfile);
		searchService.saveToIndex(mapper.map(existingProfile));
	}
}