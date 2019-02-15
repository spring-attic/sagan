package sagan.projects.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.support.JsonPController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

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
    private final ProjectPatchingService projectPatchingService;

    @Autowired
    public ProjectMetadataController(ProjectMetadataService service,
            ProjectPatchingService projectPatchingService) {
        this.service = service;
        this.projectPatchingService = projectPatchingService;
    }

    @RequestMapping(value = "/{projectId}", method = { GET, HEAD })
    public Project projectMetadata(@PathVariable("projectId") String projectId) throws IOException {
        Project project = service.getProject(projectId);
        return project;
    }

    @RequestMapping(value = "/{projectId}/releases/{version:.*}", method = GET)
    public ProjectRelease releaseMetadata(@PathVariable("projectId") String projectId,
                                          @PathVariable("version") String version) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        ProjectRelease release = project.getProjectRelease(version);
        if (release == null) {
            throw new MetadataNotFoundException("Could not find " + version + " for " + projectId);
        }
        return release.createWithVersionPattern();
    }

    @RequestMapping(value = "/{projectId}/releases", method = GET)
    public List<ProjectRelease> releaseMetadata(@PathVariable("projectId") String projectId) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        List<ProjectRelease> releases = new ArrayList<>();
        for (ProjectRelease release : project.getProjectReleases()) {
            releases.add(release.createWithVersionPattern());
        }
        return releases;
    }

    @RequestMapping(value = "/{projectId}/releases", method = PUT)
    public Project updateProjectMetadata(@PathVariable("projectId") String projectId,
                                         @RequestBody List<ProjectRelease> releases)
            throws IOException {
        Project project = service.getProject(projectId);
        for (ProjectRelease release : releases) {
            project.updateProjectRelease(release);
        }
        service.save(project);
        return project;
    }

    @RequestMapping(value = "/{projectId}/releases", method = POST)
    public ProjectRelease updateReleaseMetadata(@PathVariable("projectId") String projectId,
                                                @RequestBody ProjectRelease release) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        boolean found = project.updateProjectRelease(release);
        if (found) {
            service.save(project);
        }
        return release;
    }

    @RequestMapping(value = "/{projectId}/releases/{version:.*}", method = DELETE)
    public ProjectRelease removeReleaseMetadata(@PathVariable("projectId") String projectId,
                                                @PathVariable("version") String version) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        ProjectRelease found = project.removeProjectRelease(version);
        if (found == null) {
            throw new MetadataNotFoundException("Could not find " + version + " for " + projectId);
        }
        service.save(project);
        return found;
    }

    @RequestMapping(value = "/{projectId}", method = PATCH)
    public Project updateProject(@PathVariable("projectId") String projectId,
            @RequestBody Project projectWithPatches) throws IOException {
        Project project = service.getProject(projectId);
        if (project == null) {
            throw new MetadataNotFoundException("Cannot find project " + projectId);
        }
        Project patchedProject = projectPatchingService.patch(projectWithPatches, project);
        return service.save(patchedProject);
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

@Service
class ProjectPatchingService {
    Project patch(Project newValue, Project valueToMutate) {
        return ObjectPatcher.patch(newValue, valueToMutate)
                .mutateIfDirty(Project::getRawOverview, Project::setRawOverview)
                .mutateIfDirty(Project::getRawBootConfig, Project::setRawBootConfig)
                .patchedValue();
    }
}

// could be reused in other projects
class ObjectPatcher<T> {
    private T newValue;
    private T valueToMutate;

    private ObjectPatcher(T newValue, T valueToMutate) {
        this.newValue = newValue;
        this.valueToMutate = valueToMutate;
    }

    static <T> ObjectPatcher<T> patch(T newValue, T valueToMutate) {
        return new ObjectPatcher<>(newValue, valueToMutate);
    }

    <V> ObjectPatcher<T> mutateIfDirty(Function<T, V> getter, BiConsumer<T, V> modifyIfDirty) {
        V newValue = getter.apply(this.newValue);
        V valueToMutate = getter.apply(this.valueToMutate);
        if (isDirty(newValue, valueToMutate)) {
            // mutates the old object
            modifyIfDirty.accept(this.valueToMutate, newValue);
        }
        return this;
    }

    // syntactic sugar since new value got mutated all the way
    T patchedValue() {
        return this.valueToMutate;
    }

    private boolean isDirty(Object newValue, Object oldValue) {
        return newValue != null && !Objects.equals(newValue, oldValue);
    }
}
