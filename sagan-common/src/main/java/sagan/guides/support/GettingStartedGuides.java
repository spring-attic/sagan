package sagan.guides.support;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import sagan.guides.*;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository providing access to and caching of documents within the
 * {@link GettingStartedGuide} Git repositories at GitHub. See
 * http://github.com/spring-guides.
 */
@Repository
public class GettingStartedGuides implements DocRepository<GettingStartedGuide>,
        ContentProvider<GettingStartedGuide>, ImageProvider {

    public static final String CACHE_NAME = "cache.guides";
    public static final String CACHE_TTL = "${cache.guides.timetolive:300}";

    static final String REPO_PREFIX = "gs-";

    private static final Logger log = LoggerFactory.getLogger(GettingStartedGuides.class);

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";

    private final GuideOrganization org;
    private final SetMultimap<String, String> tagMultimap = LinkedHashMultimap.create();
    private final ProjectMetadataService projectMetadataService;
    private final AsciidoctorUtils asciidoctorUtils = new AsciidoctorUtils();

    @Autowired
    public GettingStartedGuides(GuideOrganization org, ProjectMetadataService projectMetadataService) {
        this.org = org;
        this.projectMetadataService = projectMetadataService;
    }

    @Override
    @Cacheable(CACHE_NAME)
    public GettingStartedGuide find(String guide) {
        String repoName = REPO_PREFIX + guide;
        String description = getRepoDescription(repoName);
        return new GettingStartedGuide(
                new DefaultGuideMetadata(
                        org.getName(), guide, repoName, description, tagMultimap.get(repoName)), this, this);
    }

    @Override
    public List<GettingStartedGuide> findAll() {
        List<GettingStartedGuide> guides = new ArrayList<>();
        for (GitHubRepo repo : org.findRepositoriesByPrefix(REPO_PREFIX)) {
            String repoName = repo.getName();
            GuideMetadata metadata =
                    new DefaultGuideMetadata(
                            org.getName(), repoName.replaceAll("^" + REPO_PREFIX, ""), repoName, repo.getDescription(),
                            tagMultimap.get(repoName));
            guides.add(new GettingStartedGuide(metadata, this, this));
        }
        return guides;
    }

    @Override
    public void populate(GettingStartedGuide guide) {
        String repoName = guide.getRepoName();

        AsciidocGuide asciidocGuide = asciidoctorUtils.getDocument(org, String.format(README_PATH_ASC, org.getName(), repoName));
        tagMultimap.putAll(guide.getRepoName(), asciidocGuide.getTags());
        guide.setContent(asciidocGuide.getContent());
        guide.setSidebar(asciidoctorUtils.generateDynamicSidebar(projectMetadataService, asciidocGuide));
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
