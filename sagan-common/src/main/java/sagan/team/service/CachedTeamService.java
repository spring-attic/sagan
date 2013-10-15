package sagan.team.service;

import sagan.team.MemberProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CachedTeamService {

    private TeamService teamService;

    //Required for @Cacheable proxy
    protected CachedTeamService() { }

    @Autowired
    public CachedTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    @Cacheable("cache.database")
    public List<MemberProfile> fetchActiveMembers() {
        return teamService.fetchActiveMembers();
    }

    @Cacheable("cache.database")
    public MemberProfile fetchMemberProfileUsername(String username) {
        return teamService.fetchMemberProfileUsername(username);
    }
}
