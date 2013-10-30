package sagan.guides.support;

import sagan.guides.ContentProvider;
import sagan.guides.DefaultGuideMetadata;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Guide;
import sagan.guides.GuideMetadata;
import sagan.guides.ImageProvider;
import sagan.util.ResourceNotFoundException;
import sagan.util.service.github.Readme;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * Repository implementation providing data access services for getting started guides.
 *
 * @author Chris Beams
 * @author Greg Turnquist
 */
@Component
public class GettingStartedGuides extends GitHubBackedGuideRepository
        implements DocRepository<GettingStartedGuide>, ContentProvider<GettingStartedGuide>, ImageProvider {

    static final String REPO_PREFIX = "gs-";

    private static final Logger log = LoggerFactory.getLogger(GettingStartedGuides.class);

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README = REPO_BASE_PATH + "/readme";
    private static final String README_PATH_MD = REPO_BASE_PATH + "/contents/README.md";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";
    private static final String SIDEBAR_PATH = REPO_BASE_PATH + "/contents/SIDEBAR.md";

    @Autowired
    public GettingStartedGuides(GuideOrganization org) {
        super(org);
    }

    @Override
    public GettingStartedGuide find(String guide) {
        String repoName = REPO_PREFIX + guide;
        String description = getRepoDescription(repoName);
        return new GettingStartedGuide(
                new DefaultGuideMetadata(org.getName(), guide, repoName, description), this, this);
    }

    @Override
    public List<GettingStartedGuide> findAll() {
        List<GettingStartedGuide> guides = new ArrayList<>();
        for (GitHubRepo repo : org.findRepositoriesByPrefix(REPO_PREFIX)) {
            String repoName = repo.getName();
            GuideMetadata metadata =
                    new DefaultGuideMetadata(org.getName(), repoName.replaceAll("^" + REPO_PREFIX, ""), repoName, repo.getDescription());
            guides.add(new GettingStartedGuide(metadata, this, this));
        }
        return guides;
    }

    @Override
    public void populate(GettingStartedGuide guide) {
        String repoName = guide.getRepoName();

        Readme readme = getGuideReadme(String.format(README, org.getName(), repoName));

        final String body;
        if (readme.getName().endsWith(".md")) {
            body = getMarkdownGuideContentAsHtml(String.format(README_PATH_MD, org.getName(), repoName));
        } else {
            body = getAsciiDocGuideContentAsHtml(String.format(README_PATH_ASC, org.getName(), repoName));
        }

        guide.setContent(body);
        guide.setSidebar(getGuideSidebar(repoName));
    }

    /**
     * Fetch the default readme file for this given guide repository, which may be written
     * in either Markdown or AsciiDoc.
     */
    private Readme getGuideReadme(String path) {
        try {
            log.debug(String.format("Fetching README for '%s'", path));
            return org.getReadme(path);
        } catch (RestClientException ex) {
            String msg = String.format("No README found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    private String getMarkdownGuideContentAsHtml(String path) {
        try {
            log.debug(String.format("Fetching getting started guide for '%s'", path));
            return org.getMarkdownFileAsHtml(path);
        } catch (RestClientException ex) {
            String msg = String.format("No getting started guide found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    private String getAsciiDocGuideContentAsHtml(String path) {
        try {
            log.debug(String.format("Fetching getting started guide for '%s'", path));
            return org.getAsciiDocFileAsHtml(path);
        } catch (RestClientException ex) {
            String msg = String.format("No getting started guide found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    private String getGuideSidebar(String repoName) {
        try {
            return org.getMarkdownFileAsHtml(String.format(SIDEBAR_PATH, org.getName(), repoName));
        } catch (RestClientException ex) {
            return "";
        }
    }

    @Override
    public byte[] loadImage(Guide guide, String imageName) {
        try {
            return org.getGuideImage(guide.getRepoName(), imageName);
        } catch (RestClientException ex) {
            String msg = String.format("Could not load image '%s' for repo '%s'", imageName, guide.getRepoName());
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

}
