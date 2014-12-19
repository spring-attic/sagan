package sagan.guides.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import sagan.guides.*;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Repository providing access to and caching of documents within the
 * {@link GettingStartedGuide} Git repositories at GitHub. See
 * http://github.com/spring-guides.
 */
@Repository
public class GettingStartedGuides implements DocRepository<GettingStartedGuide, GuideMetadata>,
        ContentProvider<GettingStartedGuide>, ImageProvider {

    public static final String CACHE_NAME = "cache.guides";
    public static final Class CACHE_TYPE = GettingStartedGuide.class;
    public static final String CACHE_TTL = "${cache.docs.timetolive:0}"; // never expires

    static final String REPO_PREFIX = "gs-";

    private static final Logger log = LoggerFactory.getLogger(GettingStartedGuides.class);

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";

    private final GuideOrganization org;
    private final MultiValueMap<String, String> tagMultimap = new LinkedMultiValueMap();
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
        Set<String> tags = tagMultimap.get(repoName) != null ? new HashSet<>(tagMultimap.get(repoName)) : Collections.emptySet();
        GettingStartedGuide gsgGuide =  new GettingStartedGuide(
                new DefaultGuideMetadata(org.getName(), guide, repoName, description, tags));
        return populate(gsgGuide);
    }

    @Override
    public List<GettingStartedGuide> findAll() {
        return findAllMetadata()
                .stream()
                    .map(m -> new GettingStartedGuide(m))
                    .map(g -> populate(g))
                .collect(Collectors.toList());
    }

    @Override
    public List<GuideMetadata> findAllMetadata() {

        return org.findRepositoriesByPrefix(REPO_PREFIX)
                .stream()
                .map(repo -> new DefaultGuideMetadata(org.getName(), repo.getName().replaceAll("^" + REPO_PREFIX, ""),
                        repo.getName(), repo.getDescription(),
                        new HashSet<String>(tagMultimap.getOrDefault(repo.getName(), Collections.emptyList()))))
                .collect(Collectors.toList());
    }

    @Override
    public GettingStartedGuide populate(GettingStartedGuide guide) {
        String repoName = guide.getRepoName();

        AsciidocGuide asciidocGuide = asciidoctorUtils.getDocument(org, String.format(README_PATH_ASC, org.getName(), repoName));
        asciidocGuide.getTags().forEach(tag -> tagMultimap.set(guide.getRepoName(), tag));
        guide.setContent(asciidocGuide.getContent());
        guide.setSidebar(asciidoctorUtils.generateDynamicSidebar(projectMetadataService, asciidocGuide));
        return guide;
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

    public String parseGuideName(String repositoryName) {
        Assert.hasText(repositoryName);
        Assert.isTrue(repositoryName.startsWith(REPO_PREFIX));
        return repositoryName.substring(REPO_PREFIX.length());
    }

    @CacheEvict(CACHE_NAME)
    public void evictFromCache(String guide) {
        // No op, this method will trigger cache eviction for the guide given as a parameter
        log.info("Guide evicted from cache: {}", guide);
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
