package sagan.guides.support;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.guides.GuideMetadata;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;

import org.springframework.social.github.api.GitHubRepo;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProjectGuidesRepositoryTest {
    ProjectGuidesRepository subject;

    Project bananaProject = new Project("banana-project", "Banana Project", "my-banana-url", "my-banana-url", null,
            "my-banana-category");
    Project specialProject = new Project("special-project", "Special Project", "my-repo-url", "my-site-url", null,
            "my-special-category");

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    private GuideOrganization guideOrganization;

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Before
    public void setUp() throws Exception {
        subject = new GettingStartedGuides(guideOrganization, projectMetadataService);
    }

    @Test
    public void findByProject() throws Exception {
        GitHubRepo awesomeGithubRepo = new GitHubRepo();
        awesomeGithubRepo.setDescription("My Special Project Guide :: Learn awesome stuff with this guide :: special-project");
        awesomeGithubRepo.setName("gs-special-guide");

        GitHubRepo bananaGithubRepo = new GitHubRepo();
        bananaGithubRepo.setDescription("Bananas Guide :: Everything you ever wondered about bananas :: banana-project");
        bananaGithubRepo.setName("gs-banana-guide");

        List<GitHubRepo> repos = Arrays.asList(awesomeGithubRepo, bananaGithubRepo);

        given(guideOrganization.getName()).willReturn("spring-guides");
        given(guideOrganization.findRepositoriesByPrefix("gs-")).willReturn(repos);
        given(projectMetadataService.getProject("banana-project")).willReturn(bananaProject);
        given(projectMetadataService.getProject("special-project")).willReturn(specialProject);

        List<GuideMetadata> guides = subject.findByProject(specialProject);

        assertThat(guides, hasSize(1));
        assertThat(guides.get(0).getRepoName(), is("gs-special-guide"));
    }
}