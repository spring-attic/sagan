package sagan.guides.support;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.springframework.social.github.api.GitHubRepo;
import sagan.guides.Tutorial;
import sagan.support.github.Readme;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Unit tests for {@link Tutorials}.
 */
public class TutorialsTests {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization org;

    private Tutorials tutorials;
    private GitHubRepo repo;
    private Readme readme;
    private AsciidocGuide tutorial;

    @Before
    public void setup() throws IOException {
        initMocks(this);
        given(org.getName()).willReturn("mock-org");
        tutorials = new Tutorials(org, null);
        repo = new GitHubRepo();
        repo.setName("tut-rest");
        repo.setDescription("Rest tutorial :: Learn some rest stuff");
        readme = new Readme();
        readme.setName("README.adoc");
        tutorial = new AsciidocGuide("REST Tutorial", "Table of C");
    }

    @Test
    public void findAll() {
        given(org.findRepositoriesByPrefix(Tutorials.REPO_PREFIX)).willReturn(singletonList(repo));
        given(org.getAsciidocGuide("/repos/mock-org/tut-rest/zipball")).willReturn(tutorial);

        List<Tutorial> all = tutorials.findAll();
        assertThat(all.size(), is(1));
        Tutorial first = all.get(0);
        assertThat(first.getGuideId(), equalTo("rest"));
        assertThat(first.getTitle(), equalTo("Rest tutorial"));
        assertThat(first.getSubtitle(), equalTo("Learn some rest stuff"));
    }

    @Test
    public void pageZero() throws IOException {
        given(org.getRepoInfo(eq("tut-rest"))).willReturn(repo);
        given(org.getReadme(eq("/repos/mock-org/tut-rest/readme"))).willReturn(readme);
        given(org.getAsciidocGuide("/repos/mock-org/tut-rest/zipball")).willReturn(tutorial);

        Tutorial tutorial = tutorials.find("rest");

        assertThat(tutorial.getContent(), equalTo("REST Tutorial"));
    }

    @Test
    public void testParseTutorialName() throws Exception {
        String name = tutorials.parseGuideName("tut-tutorial-name");
        assertThat(name, is("tutorial-name"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmptyTutorialName() throws Exception {
        tutorials.parseGuideName("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseInvalidTutorialName() throws Exception {
        tutorials.parseGuideName("invalid");
    }

}
