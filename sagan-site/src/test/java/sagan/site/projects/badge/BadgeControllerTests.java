/*
 * Copyright 2016-2017 the original author or authors.
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

package sagan.site.projects.badge;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sagan.site.projects.Project;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.projects.Release;
import sagan.site.projects.Version;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BadgeControllerTests {

    @Mock
    private ProjectMetadataService projectMetadataServiceMock;

    private VersionBadgeService versionBadgeService = new VersionBadgeService();

    private BadgeController controller;
    private Project project;
    private SortedSet<Release> releases = new TreeSet<>();

    @BeforeEach
    public void setUp() throws Exception {
        this.versionBadgeService.postConstruct();
		this.controller = new BadgeController(projectMetadataServiceMock, versionBadgeService);
		this.project = new Project("spring-data-redis", "Spring Data Redis");
		this.project.setReleases(this.releases);
        when(this.projectMetadataServiceMock.fetchFullProject("spring-data-redis")).thenReturn(project);
    }

    @AfterEach
    public void tearDown() throws Exception {
		this.versionBadgeService.preDestroy();
    }

    @Test
    public void badgeNotFound() throws Exception {
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void badgeShouldBeGenerated() throws Exception {
		this.releases.add(new Release(Version.of("1.0.RELEASE"), true));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getETag()).isEqualTo("\"1.0.RELEASE\"");
        assertThat(response.getHeaders().getCacheControl()).isEqualTo("max-age=3600");

        String content = new String(response.getBody());
        assertThat(content).contains("<svg");
        assertThat(content).contains("Spring Data Redis");
        assertThat(content).contains("1.0.RELEASE");
    }

    @Test
    public void projecWithTwoReleasesShouldBeGenerated() throws Exception {
		this.releases.add(new Release(Version.of("1.0.RELEASE"), false));
		this.releases.add(new Release(Version.of("1.1.RELEASE"), true));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        String content = new String(response.getBody());
        assertThat(content).contains("1.1.RELEASE");
    }

    @Test
    public void projecWithTwoReleasesWithoutCurrentFlagPicksHighestRelease() throws Exception {
		this.releases.add(new Release(Version.of("1.0.RELEASE"), false));
		this.releases.add(new Release(Version.of("1.1.RELEASE"), false));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        String content = new String(response.getBody());
        assertThat(content).contains("1.1.RELEASE");
    }

    @Test
    public void projecWithTwoReleasesFlagPicksCurrentRelease() throws Exception {
		this.releases.add(new Release(Version.of("1.0.RELEASE"), true));
		this.releases.add(new Release(Version.of("1.1.RELEASE"), false));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        String content = new String(response.getBody());
        assertThat(content).contains("1.0.RELEASE");
    }

    @Test
    public void projecWithTwoReleasesUsingSymbolicNamesWithNumbersWithoutCurrentFlagPicksMostRecentRelease()
            throws Exception {
		this.releases.add(new Release(Version.of("Angel-SR6"), false));
		this.releases.add(new Release(Version.of("Brixton-SR2"), false));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        String content = new String(response.getBody());
        assertThat(content).contains("Brixton-SR2");
    }

    @Test
    public void projecWithTwoReleasesUsingSymbolicNamesWithoutCurrentFlagPicksFirstRelease() throws Exception {
		this.releases.add(new Release(Version.of("Angel-SR6"), false));
		this.releases.add(new Release(Version.of("Brixton-RELEASE"), false));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        String content = new String(response.getBody());
        assertThat(content).contains("Brixton-RELEASE");
    }

    @Test
    public void projecWithTwoReleasesUsingSymbolicNamesFlagPicksCurrentRelease() throws Exception {
		this.releases.add(new Release(Version.of("Angel-SR1"), false));
		this.releases.add(new Release(Version.of("Brixton-RELEASE"), true));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        String content = new String(response.getBody());
        assertThat(content).contains("Brixton-RELEASE");
    }
}
