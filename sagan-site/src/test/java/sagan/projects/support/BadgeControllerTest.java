/*
 * Copyright 2016 the original author or authors.
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

package sagan.projects.support;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;

/**
 * @author Mark Paluch
 */
@RunWith(MockitoJUnitRunner.class)
public class BadgeControllerTest {

    @Mock
    private ProjectMetadataService projectMetadataServiceMock;

    private VersionBadgeService versionBadgeService = new VersionBadgeService();

    private BadgeController controller;
    private Project project;
    private List<ProjectRelease> releases = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        versionBadgeService.postConstruct();
        controller = new BadgeController(projectMetadataServiceMock, versionBadgeService);
        project = new Project("spring-data-redis", "Spring Data Redis", "http", "http", releases, false, "data");
        when(projectMetadataServiceMock.getProject("spring-data-redis")).thenReturn(project);
    }

    @After
    public void tearDown() throws Exception {
        versionBadgeService.preDestroy();
    }

    @Test
    public void badgeForReleaseNotFound() throws Exception {

        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");
        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.NOT_FOUND)));
    }

    @Test
    public void badgeForReleaseShouldBeGenerated() throws Exception {

        releases.add(new ProjectRelease("1.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "", "", "", ""));
        ResponseEntity<byte[]> response = controller.releaseBadge("spring-data-redis");

        assertThat(response.getStatusCode(), is(equalTo(HttpStatus.OK)));

        String content = new String(response.getBody());
        assertThat(content, containsString("<svg"));
        assertThat(content, containsString("Spring Data Redis"));
        assertThat(content, containsString("1.0.RELEASE"));
    }
}
