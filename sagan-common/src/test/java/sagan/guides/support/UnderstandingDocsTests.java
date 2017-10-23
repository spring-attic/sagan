package sagan.guides.support;

import sagan.guides.UnderstandingDoc;
import sagan.support.github.RepoContent;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link UnderstandingDocs}
 */
public class UnderstandingDocsTests {

    private ArrayList<RepoContent> repoContents = new ArrayList<>();
    private GuideOrganization org = mock(GuideOrganization.class);
    private UnderstandingDocs understandingGuidesService = new UnderstandingDocs(org, "my-udocs-repo");

    @Before
    public void setUp() {
        addRepoContent(repoContents, "foo", "foo", "dir");
        addRepoContent(repoContents, "rest", "rest", "dir");
        addRepoContent(repoContents, "README.src", "README.src", "file");
        addRepoContent(repoContents, "README.md", "README.md", "file");
        given(org.getRepoContents("understanding")).willReturn(repoContents);
    }

    @Test
    public void returnsOnlyDirContents() {
        List<UnderstandingDoc> guides = understandingGuidesService.findAll();

        assertThat(guides.size(), equalTo(2));
        assertThat(guides.get(0).getSubject(), equalTo("foo"));
        assertThat(guides.get(1).getSubject(), equalTo("rest"));
    }

    @Test
    public void testGetsContentForGuide() throws Exception {
        given(org.getMarkdownFileAsHtml(matches(".*foo.*README.*"))).willReturn("Understanding: foo!");
        given(org.getMarkdownFileAsHtml(matches(".*rest.*README.*"))).willReturn("Understanding: rest");

        List<UnderstandingDoc> guides = understandingGuidesService.findAll();

        assertThat(guides.get(0).getContent(), equalTo("Understanding: foo!"));
        assertThat(guides.get(1).getContent(), equalTo("Understanding: rest"));
    }

    private void addRepoContent(ArrayList<RepoContent> repoContents, String name, String path, String type) {
        RepoContent repoContent = new RepoContent();
        repoContent.setName(name);
        repoContent.setPath(path);
        repoContent.setType(type);
        repoContents.add(repoContent);
    }
}
