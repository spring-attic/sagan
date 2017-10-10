package sagan.guides.support;

import sagan.guides.AbstractGuide;
import sagan.guides.ContentProvider;
import sagan.guides.DefaultGuideMetadata;
import sagan.guides.GuideMetadata;
import sagan.guides.ImageProvider;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

/**
 * Repository implementation providing data access services for guides by repo prefix.
 */
public abstract class PrefixDocRepository<T extends AbstractGuide> implements DocRepository<T, GuideMetadata>,
        ContentProvider<T>, ImageProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";

    private final GuideOrganization org;
    private final ProjectMetadataService projectMetadataService;
    private final AsciidoctorUtils asciidoctorUtils = new AsciidoctorUtils();

    private String prefix;

    public PrefixDocRepository(GuideOrganization org, ProjectMetadataService projectMetadataService, String prefix) {
        this.org = org;
        this.projectMetadataService = projectMetadataService;
        this.prefix = prefix;
    }

    @Override
    public T find(String tutorial) {
        return populate(create(findMetadata(tutorial)));
    }

    public void evictFromCache(String guide) {
        log.info("Tutorial evicted from cache: {}", guide);
    }

    protected abstract T create(GuideMetadata findMetadata);

    public GuideMetadata findMetadata(String tutorial) {
        String repoName = this.prefix + tutorial;
        String description = getRepoDescription(repoName);
        return new DefaultGuideMetadata(org.getName(), tutorial, repoName, description);
    }

    @Override
    public List<GuideMetadata> findAllMetadata() {

        return org.findRepositoriesByPrefix(this.prefix)
                .stream()
                .map(repo -> new DefaultGuideMetadata(org.getName(), repo.getName().replaceAll("^" + this.prefix, ""),
                        repo.getName(), repo.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<T> findAll() {

        return findAllMetadata()
                .stream()
                .map(this::create)
                .map(this::populate)
                .collect(Collectors.toList());
    }

    @Override
    public T populate(T tutorial) {
        String repoName = tutorial.getRepoName();

        AsciidocGuide asciidocGuide = asciidoctorUtils.getDocument(org, String.format(README_PATH_ASC, org.getName(),
                repoName));
        tutorial.setContent(asciidocGuide.getContent());
        tutorial.setSidebar(asciidoctorUtils.generateDynamicSidebar(projectMetadataService, asciidocGuide));
        return tutorial;
    }

    @Override
    public byte[] loadImage(GuideMetadata tutorialMetadata, String imageName) {
        try {
            return org.getGuideImage(tutorialMetadata.getRepoName(), imageName);
        } catch (RestClientException ex) {
            String msg = String.format("Could not load image '%s' for repo '%s'", imageName, tutorialMetadata
                    .getRepoName());
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    public String parseGuideName(String repositoryName) {
        Assert.hasText(repositoryName);
        Assert.isTrue(repositoryName.startsWith(this.prefix));
        return repositoryName.substring(this.prefix.length());
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
