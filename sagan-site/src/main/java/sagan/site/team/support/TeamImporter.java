package sagan.site.team.support;

import org.springframework.social.github.api.GitHub;

interface TeamImporter {
    void importTeamMembers(GitHub gitHub);
}
