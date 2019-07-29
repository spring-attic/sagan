package sagan.team.support;

import sagan.search.SearchEntryMapper;
import sagan.search.types.SitePage;
import sagan.team.MemberProfile;

import org.springframework.stereotype.Component;

@Component
class MemberProfileSearchEntryMapper implements SearchEntryMapper<MemberProfile> {
    @SuppressWarnings("unchecked")
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
