package sagan.site.webapi.generation;

import sagan.site.projects.Project;
import sagan.site.projects.ProjectGeneration;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.webapi.project.ProjectMetadataController;
import sagan.support.ResourceNotFoundException;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequestMapping(path = "/api", produces = MediaTypes.HAL_JSON_VALUE)
@ExposesResourceFor(GenerationMetadata.class)
public class GenerationMetadataController {

	private final ProjectMetadataService metadataService;

	private final GenerationMetadataAssembler resourceAssembler;

	public GenerationMetadataController(ProjectMetadataService metadataService, GenerationMetadataAssembler resourceAssembler) {
		this.metadataService = metadataService;
		this.resourceAssembler = resourceAssembler;
	}

	@GetMapping("/projects/{projectId}/generations")
	public ResponseEntity<CollectionModel<GenerationMetadata>> listGenerations(@PathVariable String projectId) {
		Project project = this.metadataService.fetchFullProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("Could not find releases for project: " + projectId);
		}
		CollectionModel<GenerationMetadata> generationMetadata = this.resourceAssembler.toCollectionModel(project.getGenerationsInfo().getGenerations());
		generationMetadata.add(linkTo(methodOn(ProjectMetadataController.class).showProject(projectId)).withRel("project"));
		long lastModified = project.getGenerationsInfo().getLastModified().toEpochSecond() * 1000;
		return ResponseEntity.ok().lastModified(lastModified).body(generationMetadata);
	}

	@GetMapping("/projects/{projectId}/generations/{name}")
	public ResponseEntity<EntityModel<GenerationMetadata>> showRelease(@PathVariable String projectId, @PathVariable String name) {
		Project project = this.metadataService.fetchFullProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("Could not find releases for project: " + projectId);
		}
		ProjectGeneration generation = project.findGeneration(name)
				.orElseThrow(() -> new ResourceNotFoundException("Could not find generation: " + name + " for project: " + projectId));
		GenerationMetadata generationMetadata = this.resourceAssembler.toModel(generation);
		long lastModified = project.getGenerationsInfo().getLastModified().toEpochSecond() * 1000;
		return ResponseEntity.ok().lastModified(lastModified).body(EntityModel.of(generationMetadata));
	}

}
