package sagan.team;

import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;

import org.springframework.stereotype.Component;

@Component
public class MemberProfileSearchEntryMapper  implements SearchEntryMapper<MemberProfile> {
    @Override
    public SearchEntry map(MemberProfile profile) {
        SearchEntry entry = new SearchEntry();
        entry.setTitle(profile.getFullName());
        entry.setSummary(profile.getBio());
        entry.setRawContent(profile.getBio());
        entry.setPath("/team/" + profile.getUsername());
        return entry;
    }
}
