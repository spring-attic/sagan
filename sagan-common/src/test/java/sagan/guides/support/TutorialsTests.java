package sagan.guides.support;

import sagan.guides.Tutorial;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;

import org.springframework.social.github.api.GitHubRepo;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link Tutorials}.
 *
 * @author Chris Beams
 */
public class TutorialsTests {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization org;

    private Tutorials tutorials;
    private GitHubRepo repo;

    @Before
    public void setup() throws IOException {
        initMocks(this);
        given(org.getName()).willReturn("mock-org");
        tutorials = new Tutorials(org);
        repo = new GitHubRepo();
        repo.setName("tut-rest");
        repo.setDescription("Rest tutorial :: Learn some rest stuff");
    }

    @Test
    public void findAll() {
        given(org.findRepositoriesByPrefix(Tutorials.REPO_PREFIX)).willReturn(singletonList(repo));

        List<Tutorial> all= tutorials.findAll();
        assertThat(all.size(), is(1));
        Tutorial first = all.get(0);
        assertThat(first.getGuideId(), equalTo("rest"));
        assertThat(first.getTitle(), equalTo("Rest tutorial"));
        assertThat(first.getSubtitle(), equalTo("Learn some rest stuff"));
    }

    @Test
    public void pageZero() throws IOException {
        given(org.getRepoInfo(eq("tut-rest"))).willReturn(repo);
        given(org.getMarkdownFileAsHtml(matches("/repos/mock-org/tut-rest/contents/README.md")))
                .willReturn("REST Tutorial Page Zero");

        Tutorial tutorial = tutorials.find("rest");

        assertThat(tutorial.getContent(), equalTo("REST Tutorial Page Zero"));
    }

    @Test
    public void pageOne() throws IOException {
        given(org.getRepoInfo(eq("tut-rest"))).willReturn(repo);
        given(org.getMarkdownFileAsHtml(matches("/repos/mock-org/tut-rest/contents/1/README.md")))
                .willReturn("REST Tutorial Page One");

        Tutorial guide = tutorials.findByPage("rest", 1);

        assertThat(guide.getContent(), equalTo("REST Tutorial Page One"));
    }

}
