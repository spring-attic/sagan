/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        assertThat(getProjectReleases(project), equalTo(asList("0.0.11.MILESTONE", "0.0.10.RELEASE",
                "0.0.9.BUILD-SNAPSHOT")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_milestonesWithVersions() throws Exception {
        Project project = getProject("0.1 M1", "0.1", "0.1 M2");
        assertThat(getProjectReleases(project), equalTo(asList("0.1", "0.1 M2", "0.1 M1")));
    }

    @Test
    public void getProjectReleases_ordersMultipleStylesOfMilestones() throws Exception {
        Project project = getProject("Gosling-RC9", "Gosling.RC10");
        assertThat(getProjectReleases(project), equalTo(asList("Gosling.RC10", "Gosling-RC9")));
    }

    @Test
    public void getProjectReleases_ordersElementsWithinAReleaseTrain() throws Exception {
        Project project = getProject("Camden.BUILD-SNAPSHOT", "Camden.M1", "Camden.RC1", "Camden.RELEASE", "Camden.SR5",
                "Camden.SR6");
        assertThat(getProjectReleases(project), equalTo(asList("Camden.SR6", "Camden.SR5", "Camden.RELEASE",
                "Camden.RC1", "Camden.M1", "Camden.BUILD-SNAPSHOT")));
    }

    @Test
    public void getProjectReleases_ordersReleaseTrainsByName() throws Exception {
        Project project = getProject("Brixton.SR7", "Camden.SR5", "Dalston.RELEASE");
        assertThat(getProjectReleases(project), equalTo(asList("Dalston.RELEASE", "Camden.SR5", "Brixton.SR7")));
    }

    @Test
    public void getProjectReleases_ordersVersionsByNumber_otherCharacters() throws Exception {
        Project project = getProject("Gosling-RC9", "Angel.RC10", "Gosling-RC10", "Angel.RC9");
        assertThat(getProjectReleases(project), equalTo(asList("Gosling-RC10", "Gosling-RC9", "Angel.RC10",
                "Angel.RC9")));
    }

    private Project getProject(String... projectReleaseStrings) {
        List<ProjectRelease> projectReleases = asList(projectReleaseStrings).stream()
                .map(release -> new ProjectRelease(release, null, false, "", "", "", ""))
                .collect(toList());

        return new Project("", "", "", "", projectReleases, "");
    }

    private List<String> getProjectReleases(Project project) {
        return project.getProjectReleases().stream()
                .map(ProjectRelease::getVersion)
                .collect(toList());
    }

}
