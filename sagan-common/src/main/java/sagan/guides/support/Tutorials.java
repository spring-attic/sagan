package sagan.guides.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import sagan.guides.*;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
class Tutorials implements DocRepository<Tutorial>, ContentProvider<Tutorial>, ImageProvider {

    static final String REPO_PREFIX = "tut-";

    private static final Log log = LogFactory.getLog(Tutorials.class);

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";

    private final GuideOrganization org;
    private final ProjectMetadataService projectMetadataService;
    private final AsciidoctorUtils asciidoctorUtils = new AsciidoctorUtils();

    @Autowired
    public Tutorials(GuideOrganization org, ProjectMetadataService projectMetadataService) {
        this.org = org;
        this.projectMetadataService = projectMetadataService;
    }

    @Override
    public Tutorial find(String guide) {
        return findByPage(guide);
    }

    public Tutorial findByPage(String guide) {
        String repoName = REPO_PREFIX + guide;
        String description;
        try {
            description = org.getRepoInfo(repoName).getDescription();
        } catch (RestClientException ex) {
            description = "";
        }
        return new Tutorial(new DefaultGuideMetadata(org.getName(), guide, repoName, description), this, this);
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
        String repoName = tutorial.getRepoName();

        AsciidocGuide asciidocGuide = asciidoctorUtils.getDocument(org, String.format(README_PATH_ASC, org.getName(), repoName));
        tutorial.setContent(asciidocGuide.getContent());
        tutorial.setSidebar(asciidoctorUtils.generateDynamicSidebar(projectMetadataService, asciidocGuide));
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
