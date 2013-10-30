package sagan.guides.support;

import org.springframework.web.client.RestClientException;

public class GitHubBackedGuideRepository {
    protected final GuideOrganization org;

    public GitHubBackedGuideRepository(GuideOrganization org) {
        this.org = org;
    }

    protected String getRepoDescription(String repoName) {
        String description;
        try {
            description = org.getRepoInfo(repoName).getDescription();
        } catch (RestClientException ex) {
            description = "";
        }
        return description;
    }
}
