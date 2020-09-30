package sagan.site.team.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sagan.site.team.MemberProfile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

/**
 * Service providing high-level, selectively cached data access and other
 * {@link MemberProfile}-related operations.
 */
@Service
public class TeamService {

    private static Log logger = LogFactory.getLog(TeamService.class);

    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public MemberProfile fetchMemberProfile(Long id) {
        return teamRepository.findById(id);
    }

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

        teamRepository.save(existingProfile);
    }

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
            profile.setHidden(true);
        }
        profile.setAvatarUrl(avatarUrl);
        profile.setName(name);
        profile.setGithubUsername(username);
        updateAvatarUrlwithGravatar(profile);
        return teamRepository.save(profile);
    }

    public void showOnlyTeamMembersWithIds(List<Long> userIds) {
        teamRepository.hideTeamMembersNotInIds(userIds);
    }

    private void updateAvatarUrlwithGravatar(MemberProfile profile) {
        if (!StringUtils.isEmpty(profile.getGravatarEmail())) {
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(profile.getGravatarEmail().getBytes());
				String hashedEmail = DatatypeConverter.printHexBinary(digest.digest());
            	profile.setAvatarUrl(String.format("https://gravatar.com/avatar/%s", hashedEmail));
			}
			catch (NoSuchAlgorithmException e) {
				logger.error("Could not find MD5 MessageDigest");
			}
        }
    }
}
