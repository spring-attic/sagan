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

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;

/**
 * Controller that handles request to version badges.
 *
 * @author Mark Paluch
 */
@RequestMapping("/badges")
@Controller
class BadgeController {

    private final ProjectMetadataService service;
    private VersionBadgeService versionBadgeService;

    @Autowired
    public BadgeController(ProjectMetadataService service, VersionBadgeService versionBadgeService) {
        this.service = service;
        this.versionBadgeService = versionBadgeService;
    }

    @RequestMapping(value = { "/{projectId}/ga.svg", "/{projectId}.svg" }, method = { GET, HEAD }, produces = "image/svg+xml")
    public ResponseEntity<byte[]> releaseBadge(@PathVariable("projectId") String projectId) throws IOException {
        return badgeFor(projectId, ReleaseStatus.GENERAL_AVAILABILITY);
    }

    @RequestMapping(value = "/{projectId}/snapshot.svg", method = { GET, HEAD }, produces = "image/svg+xml")
    public ResponseEntity<byte[]> snapshotBadge(@PathVariable("projectId") String projectId) throws IOException {
        return badgeFor(projectId, ReleaseStatus.SNAPSHOT);
    }

    @RequestMapping(value = "/{projectId}/prerelease.svg", method = { GET, HEAD }, produces = "image/svg+xml")
    public ResponseEntity<byte[]> prereleaseBadge(@PathVariable("projectId") String projectId) throws IOException {
        return badgeFor(projectId, ReleaseStatus.PRERELEASE);
    }

    @RequestMapping(value = "/{projectId}/latest.svg", method = { GET, HEAD }, produces = "image/svg+xml")
    public ResponseEntity<byte[]> latestBadge(@PathVariable("projectId") String projectId) throws IOException {
        return badgeFor(projectId, null);
    }

    /**
     * Creates a SVG badge for a project with a given {@link ReleaseStatus}.
     *
     * @param projectId
     * @param releaseStatus
     * @return
     * @throws IOException
     */
    private ResponseEntity<byte[]> badgeFor(String projectId, ReleaseStatus releaseStatus) throws IOException {

        Project project = service.getProject(projectId);

        if (project == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<ProjectRelease> gaRelease = getRelease(project.getProjectReleases(),
                projectRelease -> projectRelease.getReleaseStatus() == releaseStatus);

        if (!gaRelease.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] svgBadge = versionBadgeService.createSvgBadge(project, gaRelease.get());
        return ResponseEntity.ok().eTag(gaRelease.get().getVersion()).cacheControl(CacheControl.maxAge(1L, TimeUnit.HOURS))
                .body(svgBadge);
    }

    private Optional<ProjectRelease> getRelease(Collection<ProjectRelease> projectReleases,
            Predicate<ProjectRelease> predicate) {

        Optional<ProjectRelease> first = projectReleases //
                .stream() //
                .filter(projectRelease -> predicate.test(projectRelease) && projectRelease.isCurrent()) //
                .findFirst();

        if (first.isPresent()) {
            return first;
        }

        return projectReleases //
                .stream() //
                .filter(projectRelease -> predicate.test(projectRelease)) //
                .findFirst();
    }

}
