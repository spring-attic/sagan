package sagan.team.support;

import sagan.DatabaseConfig;
import sagan.search.support.SearchService;
import sagan.team.MemberProfile;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service providing high-level, selectively cached data access and other
 * {@link MemberProfile}-related operations.
 */
@Service
class TeamService {
    private final TeamRepository teamRepository;
    private final SearchService searchService;
    private final MemberProfileSearchEntryMapper mapper;

    private static Log logger = LogFactory.getLog(TeamService.class);

    @Autowired
    public TeamService(TeamRepository teamRepository, SearchService searchService,
                       MemberProfileSearchEntryMapper mapper) {
        this.teamRepository = teamRepository;
        this.searchService = searchService;
        this.mapper = mapper;
    }

    public MemberProfile fetchMemberProfile(Long id) {
        return teamRepository.findById(id);
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public MemberProfile fetchMemberProfileUsername(String username) {
        MemberProfile profile = teamRepository.findByUsername(username);
        if (profile == null) {
            profile = MemberProfile.NOT_FOUND;
        }
        return profile;
    }

    public void updateMemberProfile(Long id, MemberProfile profile) {
        updateMemberProfile(profile, fetchMemberProfile(id));
    }

    public void updateMemberProfile(String username, MemberProfile updatedProfile) {
        updateMemberProfile(updatedProfile, fetchMemberProfileUsername(username));
    }

    private void updateMemberProfile(MemberProfile profile, MemberProfile existingProfile) {
        existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
        existingProfile.setTwitterUsername(profile.getTwitterUsername());
        existingProfile.setBio(profile.getBio());
        existingProfile.setName(profile.getName());
        existingProfile.setJobTitle(profile.getJobTitle());
        existingProfile.setTwitterUsername(profile.getTwitterUsername());
        existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
        existingProfile.setLanyrdUsername(profile.getLanyrdUsername());
        existingProfile.setGplusId(profile.getGplusId());
        existingProfile.setLocation(profile.getLocation());
        existingProfile.setGeoLocation(profile.getGeoLocation());
        existingProfile.setVideoEmbeds(profile.getVideoEmbeds());
        existingProfile.setGravatarEmail(profile.getGravatarEmail());

        if (!StringUtils.isEmpty(profile.getGravatarEmail())) {
            Md5PasswordEncoder encoder = new Md5PasswordEncoder();
            String hashedEmail = encoder.encodePassword(profile.getGravatarEmail(), null);
            existingProfile.setAvatarUrl(String.format("http://gravatar.com/avatar/%s", hashedEmail));
        }

        teamRepository.save(existingProfile);
        try {
            searchService.saveToIndex(mapper.map(existingProfile));
        } catch (Exception e) {
            logger.warn("Indexing failed for " + existingProfile.getId(), e);
        }
    }

    @Cacheable(DatabaseConfig.CACHE_NAME)
    public List<MemberProfile> fetchActiveMembers() {
        return teamRepository.findByHiddenOrderByNameAsc(false);
    }

    public List<MemberProfile> fetchHiddenMembers() {
        return teamRepository.findByHiddenOrderByNameAsc(true);
    }

    public MemberProfile createOrUpdateMemberProfile(Long githubId, String username, String avatarUrl, String name) {
        MemberProfile profile = teamRepository.findByGithubId(githubId);

        if (profile == null) {
            profile = new MemberProfile();
            profile.setGithubId(githubId);
            profile.setUsername(username);
            profile.setAvatarUrl(avatarUrl);
            profile.setName(name);
            profile.setHidden(true);
        }

        profile.setGithubUsername(username);
        return teamRepository.save(profile);
    }

    public void showOnlyTeamMembersWithIds(List<Long> userIds) {
        teamRepository.hideTeamMembersNotInIds(userIds);
    }
}
