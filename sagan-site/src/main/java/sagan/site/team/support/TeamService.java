package sagan.site.team.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.site.team.MemberProfile;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service providing high-level, selectively cached data access and other
 * {@link MemberProfile}-related operations.
 */
@Service
public class TeamService {

    private static Logger logger = LoggerFactory.getLogger(TeamService.class);

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Optional<MemberProfile> fetchMemberProfile(Long id) {
        return this.teamRepository.findByGithubId(id);
    }

    public Optional<MemberProfile> fetchMemberProfile(String username) {
        return this.teamRepository.findByUsername(username);
    }

    public Optional<MemberProfile> updateMemberProfile(Long id, MemberProfile newProfile) {
		return fetchMemberProfile(id).map(profile -> updateMemberProfile(newProfile, profile));
    }

    public Optional<MemberProfile> updateMemberProfile(String username, MemberProfile updatedProfile) {
		return fetchMemberProfile(username).map(profile -> updateMemberProfile(updatedProfile, profile));
    }

    private MemberProfile updateMemberProfile(MemberProfile profile, MemberProfile existingProfile) {
        existingProfile.setBio(profile.getBio());
        existingProfile.setName(profile.getName());
        existingProfile.setJobTitle(profile.getJobTitle());
        existingProfile.setTwitterUsername(profile.getTwitterUsername());
        existingProfile.setSpeakerdeckUsername(profile.getSpeakerdeckUsername());
        existingProfile.setLanyrdUsername(profile.getLanyrdUsername());
        existingProfile.setLocation(profile.getLocation());
        existingProfile.setGeoLocation(profile.getGeoLocation());
        existingProfile.setVideoEmbeds(profile.getVideoEmbeds());
        existingProfile.setGravatarEmail(profile.getGravatarEmail());
        existingProfile.setHidden(profile.isHidden());
        updateAvatarUrlwithGravatar(existingProfile);

        return this.teamRepository.save(existingProfile);
    }

    public List<MemberProfile> fetchActiveMembers() {
        return this.teamRepository.findByHiddenOrderByNameAsc(false);
    }

    public List<MemberProfile> fetchHiddenMembers() {
        return this.teamRepository.findByHiddenOrderByNameAsc(true);
    }

    public MemberProfile createOrUpdateMemberProfile(Long githubId, OAuth2User oAuth2User) {
        MemberProfile profile = teamRepository.findByGithubId(githubId).orElseGet(() -> {
			MemberProfile newProfile = new MemberProfile();
			newProfile.setGithubId(githubId);
			newProfile.setHidden(true);
			return newProfile;
		});
		profile.setUsername(oAuth2User.getAttribute("login"));
        profile.setGithubUsername(oAuth2User.getAttribute("login"));
        profile.setName(oAuth2User.getAttribute("name"));
        profile.setAvatarUrl(oAuth2User.getAttribute("avatar_url"));
        return this.teamRepository.save(profile);
    }

    public void showOnlyTeamMembersWithIds(List<Long> userIds) {
        this.teamRepository.hideTeamMembersNotInIds(userIds);
    }

    private void updateAvatarUrlwithGravatar(MemberProfile profile) {
        if (!StringUtils.isEmpty(profile.getGravatarEmail())) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(profile.getGravatarEmail().getBytes());
				String hashedEmail = DatatypeConverter.printHexBinary(digest.digest());
            	profile.setAvatarUrl(String.format("https://gravatar.com/avatar/%s", hashedEmail.toLowerCase()));
			}
			catch (NoSuchAlgorithmException e) {
				logger.error("Could not find MD5 MessageDigest");
			}
        }
    }
}
