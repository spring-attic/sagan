package sagan.guides.support;

import sagan.guides.ContentProvider;
import sagan.guides.UnderstandingDoc;
import sagan.support.ResourceNotFoundException;
import sagan.support.github.RepoContent;

import java.util.ArrayList;
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
class UnderstandingDocs implements DocRepository<UnderstandingDoc>, ContentProvider<UnderstandingDoc> {

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
    public UnderstandingDoc find(String subject) {
        UnderstandingDoc doc = new UnderstandingDoc(subject, this);
        populate(doc);
        return doc;
    }

    /**
     * Find all understanding documents, leaving content unpopulated to be lazily loaded
     * if and when necessary.
     */
    @Override
    public List<UnderstandingDoc> findAll() {
        return org.getRepoContents("understanding").stream()
                .filter(RepoContent::isDirectory)
                .map(repoContent -> new UnderstandingDoc(repoContent.getName(), this))
                .collect(Collectors.toList());
    }

    @Override
    public void populate(UnderstandingDoc document) {
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
    }

}
