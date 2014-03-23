package sagan.guides.support;

import sagan.guides.ContentProvider;
import sagan.guides.DefaultGuideMetadata;
import sagan.guides.Guide;
import sagan.guides.GuideMetadata;
import sagan.guides.ImageProvider;
import sagan.guides.Tutorial;
import sagan.support.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
class Tutorials implements DocRepository<Tutorial>, ContentProvider<Tutorial>, ImageProvider {

    static final String REPO_PREFIX = "tut-";

    private static final Log log = LogFactory.getLog(Tutorials.class);

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README_PATH_MD = REPO_BASE_PATH + "/contents/README.md";
    private static final String SIDEBAR_PATH = REPO_BASE_PATH + "/contents/SIDEBAR.md";
    private static final String TUTORIAL_PAGE_PATH = REPO_BASE_PATH + "/contents/%d/README.md";

    private final GuideOrganization org;

    @Autowired
    public Tutorials(GuideOrganization org) {
        this.org = org;
    }

    @Override
    public Tutorial find(String guide) {
        return findByPage(guide, 0);
    }

    public Tutorial findByPage(String guide, int page) {
        String repoName = REPO_PREFIX + guide;
        String description;
        try {
            description = org.getRepoInfo(repoName).getDescription();
        } catch (RestClientException ex) {
            description = "";
        }
        return new Tutorial(new DefaultGuideMetadata(org.getName(), guide, repoName, description), this, this, page);
    }

    @Override
    public List<Tutorial> findAll() {
        List<Tutorial> guides = new ArrayList<>();
        for (GitHubRepo repo : org.findRepositoriesByPrefix(REPO_PREFIX)) {
            String repoName = repo.getName();
            GuideMetadata metadata =
                    new DefaultGuideMetadata(org.getName(), repoName.replaceAll("^" + REPO_PREFIX, ""), repoName, repo
                            .getDescription());
            guides.add(new Tutorial(metadata, this, this));
        }
        return guides;
    }

    @Override
    public void populate(Tutorial tutorial) {
        tutorial.setContent(
                loadBodyHtml(
                tutorial.getPage() == 0 ?
                        String.format(README_PATH_MD, org.getName(), tutorial.getRepoName()) :
                        String.format(TUTORIAL_PAGE_PATH, org.getName(), tutorial.getRepoName(), tutorial.getPage())
                ));
        tutorial.setSidebar(loadSidebarHtml(tutorial.getRepoName()));
    }

    private String loadBodyHtml(String path) {
        try {
            log.debug(String.format("Fetching tutorial HTML for '%s'", path));
            return org.getMarkdownFileAsHtml(path);
        } catch (RestClientException ex) {
            String msg = String.format("No tutorial HTML found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    private String loadSidebarHtml(String repoName) {
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
