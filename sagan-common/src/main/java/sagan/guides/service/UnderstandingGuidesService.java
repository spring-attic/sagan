package sagan.guides.service;

import sagan.guides.UnderstandingGuide;
import sagan.guides.UnderstandingGuideNotFoundException;
import sagan.util.service.github.GitHubService;
import sagan.util.service.github.RepoContent;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
public class UnderstandingGuidesService {
    private static final String CONTENT_PATH = "/repos/spring-guides/understanding/contents/%s/README.md";
    private static final String SIDEBAR_PATH = "/repos/spring-guides/understanding/contents/%s/SIDEBAR.md";

    private final GitHubService gitHubService;

    @Autowired
    public UnderstandingGuidesService(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    public UnderstandingGuide getGuideForSubject(String subject) {
        return new UnderstandingGuide(subject, getContent(subject), getSidebar(subject));
    }

    private String getContent(String subject) {
        try {
            return this.gitHubService.getMarkdownFileAsHtml(String.format(CONTENT_PATH, subject.toLowerCase()));
        } catch (RestClientException e) {
            String msg = String.format("No getting started guide found for '%s'", subject);
            throw new UnderstandingGuideNotFoundException(msg, e);
        }
    }

    private String getSidebar(String subject) {
        try {
            return this.gitHubService.getMarkdownFileAsHtml(String.format(SIDEBAR_PATH, subject.toLowerCase()));
        } catch (RestClientException e) {
            return "";
        }
    }

    public List<UnderstandingGuide> getGuides() {
        List<RepoContent> repoContents = this.gitHubService.getRepoContents("understanding");
        List<UnderstandingGuide> understandingGuides = new ArrayList<>();
        for (RepoContent repoContent : repoContents) {
            if (repoContent.isDirectory()) {
                String content = getContent(repoContent.getName());
                String sidebar = getSidebar(repoContent.getName());
                understandingGuides.add(new UnderstandingGuide(repoContent.getName(), content, sidebar));
            }
        }
        return understandingGuides;
    }
}
