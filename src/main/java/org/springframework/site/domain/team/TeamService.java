package org.springframework.site.domain.team;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.site.search.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TeamService {
	private final TeamRepository teamRepository;
	private final SearchService searchService;
	private final MemberProfileSearchEntryMapper mapper;

	private static Log logger = LogFactory.getLog(TeamService.class);


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
		existingProfile.setGravatarEmail(profile.getGravatarEmail());

		if (!StringUtils.isEmpty(profile.getGravatarEmail())) {
			PasswordEncoder encoder = new Md5PasswordEncoder();
			String hashedEmail = encoder.encodePassword(profile.getGravatarEmail(), null);
			existingProfile.setAvatarUrl(String.format("http://gravatar.com/avatar/%s", hashedEmail));
		}

		teamRepository.save(existingProfile);
		try {
			searchService.saveToIndex(mapper.map(existingProfile));
		} catch (Exception e) {
			logger.warn("Indexing failed for " + existingProfile.getMemberId(), e);
		}
	}
}