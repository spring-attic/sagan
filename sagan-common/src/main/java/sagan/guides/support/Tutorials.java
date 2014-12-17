package sagan.guides.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import sagan.guides.*;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository implementation providing data access services for tutorial guides.
 */
@Component
public class Tutorials implements DocRepository<Tutorial, GuideMetadata>, ContentProvider<Tutorial>, ImageProvider {

    static final String REPO_PREFIX = "tut-";

    private static final Log log = LogFactory.getLog(Tutorials.class);

    public static final String CACHE_NAME = "cache.tutorials";
    public static final Class CACHE_TYPE = Tutorial.class;
    public static final String CACHE_TTL = "${cache.tutorials.timetolive:300}";

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
    @Cacheable(value = CACHE_NAME)
    public Tutorial find(String guide) {
        return findByPage(guide);
    }

    @Cacheable(value = CACHE_NAME)
    public Tutorial findByPage(String guide) {
        return populate(new Tutorial(findMetadata(guide)));
    }

    public GuideMetadata findMetadata(String guide) {
        String repoName = REPO_PREFIX + guide;
        String description;
        try {
            description = org.getRepoInfo(repoName).getDescription();
        } catch (RestClientException ex) {
            description = "";
        }
        return new DefaultGuideMetadata(org.getName(), guide, repoName, description);
    }

    @Override
    public List<GuideMetadata> findAllMetadata() {

        return org.findRepositoriesByPrefix(REPO_PREFIX)
                .stream()
                .map(repo -> new DefaultGuideMetadata(org.getName(), repo.getName().replaceAll("^" + REPO_PREFIX, ""),
                        repo.getName(), repo.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tutorial> findAll() {

        return findAllMetadata()
                .stream()
                    .map( m -> new Tutorial(m))
                    .map( t -> populate(t))
                .collect(Collectors.toList());
    }

    @Override
    public Tutorial populate(Tutorial tutorial) {
        String repoName = tutorial.getRepoName();

        AsciidocGuide asciidocGuide = asciidoctorUtils.getDocument(org, String.format(README_PATH_ASC, org.getName(), repoName));
        tutorial.setContent(asciidocGuide.getContent());
        tutorial.setSidebar(asciidoctorUtils.generateDynamicSidebar(projectMetadataService, asciidocGuide));
        return tutorial;
    }

    @Override
    public byte[] loadImage(GuideMetadata guide, String imageName) {
        try {
            return org.getGuideImage(guide.getRepoName(), imageName);
        } catch (RestClientException ex) {
            String msg = String.format("Could not load image '%s' for repo '%s'", imageName, guide.getRepoName());
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

}
