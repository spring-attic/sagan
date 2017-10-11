package sagan.projects;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static sagan.projects.ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY;

public class ProjectTest {

    Project project;
    ProjectRelease currentRelease = new ProjectRelease("1.8.7.RELEASE", GENERAL_AVAILABILITY, true, "ref-doc-url", "api-doc-url", "group-id", "artifact-id");
    ProjectRelease olderRelease = new ProjectRelease("2.0.0.RELEASE", GENERAL_AVAILABILITY, false, "ref-doc-url", "api-doc-url", "group-id", "artifact-id");


    @Before
    public void setup() {
        List<ProjectRelease> releases = Arrays.asList(currentRelease, olderRelease);

        project = new Project("id", "my-special-project", "my-repo-url", "my-site-url", releases, false, "my-special-category");
    }

    @Test
    public void currentVersion() {
        assertThat(project.getMostCurrentRelease(), is(Optional.of(currentRelease)));
    }

    @Test
    public void currentVersionNotAvailable() {
        List<ProjectRelease> releases = Arrays.asList();

        Project project = new Project("id", "my-special-project", "my-repo-url", "my-site-url", releases, false, "my-special-category");

        Optional<String> noVersion = Optional.empty();
        assertThat(project.getMostCurrentRelease(), is(noVersion));
    }

    @Test
    public void nonMostCurrentVersions() {
        assertThat(project.getNonMostCurrentReleases(), contains(olderRelease));
    }
}