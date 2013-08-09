package org.springframework.site.domain.team;

import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;
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
