package sagan.guides.support;

import sagan.guides.AbstractGuide;
import sagan.guides.ContentProvider;
import sagan.guides.DefaultGuideMetadata;
import sagan.guides.Guide;
import sagan.guides.GuideMetadata;
import sagan.guides.ImageProvider;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;

/**
 * Repository implementation providing data access services for guides by repo prefix.
 */
public abstract class PrefixDocRepository<T extends Guide> implements DocRepository<T, GuideMetadata>,
        ContentProvider<T>, ImageProvider, ProjectGuidesRepository {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";

    private final GuideOrganization org;
    private final AsciidoctorUtils asciidoctorUtils = new AsciidoctorUtils();

    private String prefix;

    public PrefixDocRepository(GuideOrganization org, ProjectMetadataService projectMetadataService, String prefix) {
        this.org = org;
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
    public List<GuideMetadata> findByProject(Project project) {
        return this.findAllMetadata()
                .stream()
                .filter(guideMetadata -> guideMetadata.getProjects().contains(project.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public T populate(T guide) {
        String repoName = guide.getRepoName();
        if (guide instanceof AbstractGuide) {
            AbstractGuide mutable = (AbstractGuide) guide;
            AsciidocGuide asciidocGuide = asciidoctorUtils.getDocument(org, String.format(README_PATH_ASC, org
                    .getName(),
                    repoName));
            mutable.setContent(asciidocGuide.getContent());
            mutable.setTableOfContents(asciidocGuide.getTableOfContents());
        }
        return guide;
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
        Assert.hasText(repositoryName, "repository name must not be empty");
        Assert.isTrue(repositoryName.startsWith(this.prefix), "repository name should start with " + this.prefix);
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
