package sagan.team.support;

import org.springframework.stereotype.Component;
import sagan.search.SearchEntryMapper;
import sagan.search.types.SitePage;
import sagan.team.MemberProfile;

@Component
class MemberProfileSearchEntryMapper implements SearchEntryMapper<MemberProfile> {
    @Override
    public SitePage map(MemberProfile profile) {
        SitePage entry = new SitePage();
        entry.setTitle(profile.getFullName());
        entry.setSummary(profile.getBio());
        entry.setRawContent(profile.getBio());
        entry.setPath("/team/" + profile.getUsername());
        return entry;
    }
}
