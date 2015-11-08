package sagan.projects;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ProjectVersionOrderTests {
    @Test
    public void getProjectReleases_ordersVersionsByNumber_major() throws Exception {
        Project project = getProject("10.0.0", "9.0.0", "11.0.0");
        assertThat(getProjectReleases(project), equalTo(asList("11.0.0", "10.0.0", "9.0.0")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_minor() throws Exception {
        Project project = getProject("0.10.0", "0.9.0", "0.11.0");
        assertThat(getProjectReleases(project), equalTo(asList("0.11.0", "0.10.0", "0.9.0")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_patch() throws Exception {
        Project project = getProject("0.0.10", "0.0.9", "0.0.11");
        assertThat(getProjectReleases(project), equalTo(asList("0.0.11", "0.0.10", "0.0.9")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_milestones() throws Exception {
        Project project = getProject("0.0.10.RELEASE", "0.0.9.BUILD-SNAPSHOT", "0.0.11.MILESTONE");
        assertThat(getProjectReleases(project), equalTo(asList("0.0.11.MILESTONE", "0.0.10.RELEASE", "0.0.9.BUILD-SNAPSHOT")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_milestonesWithVersions() throws Exception {
        Project project = getProject("0.1 M1", "0.1", "0.1 M2");
        assertThat(getProjectReleases(project), equalTo(asList("0.1 M2", "0.1 M1", "0.1")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_otherCharacters() throws Exception {
        Project project = getProject("Gosling-RC9", "Angel.RC10", "Gosling-RC10", "Angel.RC9");
        assertThat(getProjectReleases(project), equalTo(asList("Angel.RC10", "Gosling-RC10", "Angel.RC9", "Gosling-RC9")));
    }

    private Project getProject(String... projectReleaseStrings) {
        List<ProjectRelease> projectReleases = asList(projectReleaseStrings).stream()
            .map(release -> new ProjectRelease(release, null, false, "", "", "", ""))
            .collect(toList());

        return new Project("", "", "", "", projectReleases, false, "");
    }

    private List<String> getProjectReleases(Project project) {
        return project.getProjectReleases().stream()
            .map(ProjectRelease::getVersion)
            .collect(toList());
    }

}
