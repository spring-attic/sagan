package sagan.guides.support;

import sagan.guides.ContentProvider;
import sagan.guides.DefaultGuideMetadata;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Guide;
import sagan.guides.GuideMetadata;
import sagan.guides.ImageProvider;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;
import sagan.support.ResourceNotFoundException;
import sagan.support.github.Readme;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * Repository implementation providing data access services for getting started guides.
 *
 * @author Chris Beams
 * @author Greg Turnquist
 */
@Component
public class GettingStartedGuides extends GitHubBackedGuideRepository
        implements DocRepository<GettingStartedGuide>, ContentProvider<GettingStartedGuide>, ImageProvider {

    public static final String CACHE_NAME = "cache.guides";
    public static final String CACHE_TTL = "${cache.guides.timetolive:300}";

    static final String REPO_PREFIX = "gs-";

    private static final Logger log = LoggerFactory.getLogger(GettingStartedGuides.class);

    private static final String REPO_BASE_PATH = "/repos/%s/%s";
    private static final String README = REPO_BASE_PATH + "/readme";
    private static final String README_PATH_MD = REPO_BASE_PATH + "/contents/README.md";
    private static final String README_PATH_ASC = REPO_BASE_PATH + "/zipball";
    private static final String SIDEBAR_PATH = REPO_BASE_PATH + "/contents/SIDEBAR.md";

    private final SetMultimap<String, String> tagMultimap = LinkedHashMultimap.create();

    private final ProjectMetadataService projectMetadataService;

    @Autowired
    public GettingStartedGuides(GuideOrganization org, ProjectMetadataService projectMetadataService) {
        super(org);
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

        Readme readme = getGuideReadme(String.format(README, org.getName(), repoName));

        if (readme.getName().endsWith(".md")) {
            guide.setContent(getMarkdownGuideContentAsHtml(String.format(README_PATH_MD, org.getName(), repoName)));
            guide.setSidebar(getGuideSidebar(repoName));
        } else {
            AsciidocGuide asciidocGuide = getAsciidocGuide(String.format(README_PATH_ASC, org.getName(), repoName));
            tagMultimap.putAll(guide.getRepoName(), asciidocGuide.getTags());
            guide.setContent(asciidocGuide.getContent());
            guide.setSidebar(generateDynamicSidebar(asciidocGuide));
        }

    }

    private String generateDynamicSidebar(AsciidocGuide asciidocGuide) {
        String sidebar = "<div class='right-pane-widget--container'>\n" +
                "<div class='related_resources'>\n";

        sidebar += "<h3>" +
                "<a name='table-of-contents' class='anchor' href='#table-of-contents'></a>" +
                "Table of contents</h3>\n";
        sidebar += asciidocGuide.getTableOfContents();

        sidebar += "</div>\n</div>\n" +
                "<div class='right-pane-widget--container'>\n" +
                "<div class='related_resources'>\n";

        if (asciidocGuide.getTags().size() > 0) {
            sidebar += "<h3>" +
                    "<a name='tags' class='anchor' href='#tags'></a>" +
                    "Tags</h3><ul class='inline'>\n";

            for (String tag : asciidocGuide.getTags()) {
                sidebar += "<li><a href='/guides?filter=" + tag + "'>" + tag + "</a></li>\n";
            }
        }

        if (asciidocGuide.getProjects().size() > 0) {
            sidebar += "</ul><h3>" +
                    "<a name='projects' class='anchor' href='#projects'></a>" +
                    "Projects</h3>\n" +
                    "<ul>\n";

            for (String project : asciidocGuide.getProjects()) {

                Project springIoProject = projectMetadataService.getProject(project);
                sidebar += "<li><a href='" + springIoProject.getSiteUrl() + "'>" + springIoProject.getName()
                        + "</a></li>\n";
            }
            sidebar += "</ul>\n";
        }

        if (asciidocGuide.getUnderstandingDocs().size() > 0) {
            sidebar += "<h3>" +
                    "<a name='concepts-and-technologies' class='anchor' href='#concepts-and-technologies'></a>" +
                    "Concepts and technologies</h3>\n" +
                    "<ul>\n";
            for (String key : asciidocGuide.getUnderstandingDocs().keySet()) {
                sidebar += "<li><a href='" + key + "'>" + asciidocGuide.getUnderstandingDocs().get(key) + "</a></li>\n";
            }
            sidebar += "</ul>\n";
        }

        sidebar += "</div>\n</div>";

        return sidebar;
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

    private AsciidocGuide getAsciidocGuide(String path) {
        try {
            log.debug(String.format("Fetching getting started guide for '%s'", path));
            return org.getAsciidocGuide(path);
        } catch (RestClientException ex) {
            String msg = String.format("No getting started guide found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    private String getGuideSidebar(String repoName) {
        try {
            String sidebar = "<div class='right-pane-widget--container'>\n" +
                    "<div class='related_resources'>\n";
            sidebar += org.getMarkdownFileAsHtml(String.format(SIDEBAR_PATH, org.getName(), repoName));
            sidebar += "</div>\n</div>";
            return sidebar;
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
