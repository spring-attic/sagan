package sagan.guides.support;

import org.springframework.cache.annotation.Cacheable;
import sagan.guides.*;
import sagan.support.ResourceNotFoundException;
import sagan.support.github.RepoContent;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/**
 * Repository implementation providing data access services for understanding docs.
 */
@Component
public class UnderstandingDocs implements DocRepository<UnderstandingDoc, UnderstandingMetadata>, ContentProvider<UnderstandingDoc> {

    public static final String CACHE_NAME = "cache.understanding";
    public static final Class CACHE_TYPE = UnderstandingDoc.class;
    public static final String CACHE_TTL = "${cache.docs.timetolive:300}";

    private static final String CONTENT_PATH = "/repos/%s/%s/contents/%s/README.md";
    private static final String SIDEBAR_PATH = "/repos/%s/%s/contents/%s/SIDEBAR.md";

    private final GuideOrganization org;
    private final String repoName;

    @Autowired
    public UnderstandingDocs(GuideOrganization org, @Value("${github.repo.understanding:understanding}") String repoName) {
        this.org = org;
        this.repoName = repoName;
    }

    /**
     * Find and eagerly populate the {@link UnderstandingDoc} for the given subject.
     *
     * @throws ResourceNotFoundException if no content exists for the subject.
     */
    @Cacheable(value = CACHE_NAME)
    public UnderstandingDoc find(String subject) {
        UnderstandingDoc doc = new UnderstandingDoc(subject);
        populate(doc);
        return doc;
    }

    @Override
    public List<UnderstandingMetadata> findAllMetadata() {
        return org.getRepoContents("understanding").stream()
                .filter(RepoContent::isDirectory)
                .map(repoContent -> new UnderstandingDoc(repoContent.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UnderstandingDoc> findAll() {
        return findAllMetadata()
                .stream()
                .map(m -> new UnderstandingDoc(m.getSubject()))
                .map(t -> populate(t))
                .collect(Collectors.toList());
    }

    @Override
    public UnderstandingDoc populate(UnderstandingDoc document) {
        String lcSubject = document.getSubject().toLowerCase();
        try {
            document.setContent(org.getMarkdownFileAsHtml(String.format(CONTENT_PATH, org.getName(), repoName,
                    lcSubject)));
            document.setSidebar(org.getMarkdownFileAsHtml(String.format(SIDEBAR_PATH, org.getName(), repoName,
                    lcSubject)));
        } catch (RestClientException ex) {
            String msg = String.format("No understanding doc found for subject '%s'", lcSubject);
            throw new ResourceNotFoundException(msg, ex);
        }
        return document;
    }

}
