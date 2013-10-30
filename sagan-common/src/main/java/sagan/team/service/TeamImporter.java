package sagan.team.service;

import org.springframework.social.github.api.GitHub;

public interface TeamImporter {
    void importTeamMembers(GitHub gitHub);
}
