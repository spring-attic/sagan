package sagan.site.webapi.release;

import java.net.URI;
import java.util.Optional;

import sagan.site.projects.Project;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.projects.Release;
import sagan.site.projects.Version;
import sagan.site.webapi.project.ProjectMetadataController;
import sagan.site.support.ResourceNotFoundException;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Expose {@link ReleaseMetadata} resources.
 */
@RestController
@RequestMapping(path = "/api/", produces = MediaTypes.HAL_JSON_VALUE)
@ExposesResourceFor(ReleaseMetadata.class)
public class ReleaseMetadataController {

	private final ProjectMetadataService metadataService;

	private final ReleaseMetadataAssembler resourceAssembler;

	public ReleaseMetadataController(ProjectMetadataService metadataService, ReleaseMetadataAssembler resourceAssembler) {
		this.metadataService = metadataService;
		this.resourceAssembler = resourceAssembler;
	}

	@GetMapping("/projects/{projectId}/releases")
	public CollectionModel<ReleaseMetadata> listReleases(@PathVariable String projectId) {
		Project project = this.metadataService.fetchFullProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("Could not find releases for project: " + projectId);
		}
		CollectionModel<ReleaseMetadata> releaseMetadata = this.resourceAssembler.toCollectionModel(project.getReleases());

		project.getCurrentRelease().ifPresent(r ->
				releaseMetadata.add(linkTo(methodOn(ReleaseMetadataController.class).showCurrentRelease(project.getId())).withRel("current")));
		releaseMetadata.add(linkTo(methodOn(ProjectMetadataController.class).showProject(project.getId())).withRel("project"));
		return releaseMetadata;
	}

	@PostMapping(path = "/projects/{projectId}/releases", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createRelease(@PathVariable String projectId, @RequestBody ReleaseMetadataInput input) {
		Project project = this.metadataService.fetchFullProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("Could not find project: " + projectId);
		}
		if (project.findRelease(Version.of(input.getVersion())).isPresent()) {
			throw new InvalidReleaseException("Release already present: " + input.getVersion());
		}
		Release newRelease = project.addRelease(Version.of(input.getVersion()));
		newRelease.setApiDocUrl(input.getApiDocUrl());
		newRelease.setRefDocUrl(input.getReferenceDocUrl());
		URI newReleaseURI = linkTo(methodOn(ReleaseMetadataController.class).showRelease(project.getId(), input.getVersion())).toUri();
		this.metadataService.save(project);
		return ResponseEntity.created(newReleaseURI).build();
	}

	@GetMapping("/projects/{projectId}/releases/current")
	public EntityModel<ReleaseMetadata> showCurrentRelease(@PathVariable String projectId) {
		Release release = this.metadataService.findCurrentRelease(projectId);
		if (release == null) {
			throw new ResourceNotFoundException("Could not find current release for project: " + projectId);
		}
		ReleaseMetadata releaseMetadata = this.resourceAssembler.toModel(release);
		return EntityModel.of(releaseMetadata);
	}


	@GetMapping("/projects/{projectId}/releases/{version}")
	public EntityModel<ReleaseMetadata> showRelease(@PathVariable String projectId, @PathVariable String version) {
		Release release = this.metadataService.findRelease(projectId, Version.of(version));
		if (release == null) {
			throw new ResourceNotFoundException("Could not find release for project: " + projectId + " and version: " + version);
		}
		ReleaseMetadata releaseMetadata = this.resourceAssembler.toModel(release);
		return EntityModel.of(releaseMetadata);
	}

	@DeleteMapping("/projects/{projectId}/releases/{version}")
	public ResponseEntity<String> deleteRelease(@PathVariable String projectId, @PathVariable String version) {
		Project project = this.metadataService.fetchFullProject(projectId);
		Release release = project.findRelease(Version.of(version))
				.orElseThrow(() -> new ResourceNotFoundException("Could not find release for project: " + projectId + " and version: " + version));
		project.removeRelease(release.getVersion());
		this.metadataService.save(project);
		return ResponseEntity.noContent().build();
	}
}
