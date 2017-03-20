package sagan.projects.support;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.support.JsonPController;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles ajax requests for project metadata, typically from the
 * individual Spring project pages managed via GitHub's "GH Pages" infrastructure at
 * http://projects.spring.io. See https://github.com/spring-projects/gh-pages#readme for
 * more information.
 */
@JsonPController
@RequestMapping("/project_metadata")
class ProjectMetadataController {

    private final ProjectMetadataService service;

    @Autowired
    public ProjectMetadataController(ProjectMetadataService service) {
        this.service = service;
    }

    @RequestMapping(value = "/{projectId}", method = { GET, HEAD })
    public Project projectMetadata(@PathVariable("projectId") String projectId) throws IOException {
        return service.getProject(projectId);
    }

    @RequestMapping(value = "/{projectId}/**", method = GET)
    public ProjectRelease releaseMetadata(@PathVariable("projectId") String projectId) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        List<ProjectRelease> releases = project.getProjectReleases();
        String path = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        String version = path.substring(("/project_metadata/" + projectId + "/").length());
        for (ProjectRelease release : releases) {
            if (release.getVersion().equals(version)) {
                return release;
            }
        }
        throw new MetadataNotFoundException("Could not find " + version + " for " + projectId);
    }

    @RequestMapping(value = "/{projectId}", method = PUT)
    public Project updateMetadata(@PathVariable("projectId") String projectId, @RequestBody Project project)
            throws IOException {
        service.save(project);
        return project;
    }

    @RequestMapping(value = "/{projectId}", method = POST)
    public ProjectRelease updateReleaseMetadata(@PathVariable("projectId") String projectId,
                                                @RequestBody ProjectRelease release) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        List<ProjectRelease> releases = service.getProject(projectId).getProjectReleases();
        boolean found = false;
        for (int i = 0; i < releases.size(); i++) {
            ProjectRelease projectRelease = releases.get(i);
            if (release.getRepository() != null && release.getRepository().equals(projectRelease.getRepository())) {
                release.setRepository(projectRelease.getRepository());
            }
            if (projectRelease.getVersion().equals(release)) {
                releases.set(i, release);
                found = true;
                break;
            }
        }
        if (!found) {
            releases.add(release);
        }
        service.save(project);
        return release;
    }

    @RequestMapping(value = "/{projectId}/**", method = DELETE)
    public ProjectRelease removeReleaseMetadata(@PathVariable("projectId") String projectId) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        List<ProjectRelease> releases = project.getProjectReleases();
        String path = ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        String version = path.substring(("/project_metadata/" + projectId + "/").length());
        boolean found = false;
        ProjectRelease release = null;
        for (int i = 0; i < releases.size(); i++) {
            ProjectRelease projectRelease = releases.get(i);
            if (projectRelease.getVersion().equals(version)) {
                release = releases.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new MetadataNotFoundException("Could not find " + version + " for " + projectId);
        }
        service.save(project);
        return release;
    }

    @ExceptionHandler(MetadataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handle() {
    }

    static class MetadataNotFoundException extends RuntimeException {

        public MetadataNotFoundException(String string) {
            super(string);
        }

    }

}
