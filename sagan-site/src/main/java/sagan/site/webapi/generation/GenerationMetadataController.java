package sagan.site.webapi.generation;

import java.util.List;

import sagan.site.projects.Project;
import sagan.site.projects.ProjectGeneration;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.webapi.project.ProjectMetadataController;
import sagan.support.ResourceNotFoundException;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 *
 */
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
	public Resources<GenerationMetadata> listGenerations(@PathVariable String projectId) {
		Project project = this.metadataService.fetchFullProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("Could not find releases for project: " + projectId);
		}
		List<GenerationMetadata> generationMetadata = this.resourceAssembler.toResources(project.getGenerations());
		Resources<GenerationMetadata> resources = new Resources<>(generationMetadata);
		resources.add(linkTo(methodOn(ProjectMetadataController.class).showProject(projectId)).withRel("project"));
		return resources;
	}

	@GetMapping("/projects/{projectId}/generations/{name}")
	public Resource<GenerationMetadata> showRelease(@PathVariable String projectId, @PathVariable String name) {
		Project project = this.metadataService.fetchFullProject(projectId);
		if (project == null) {
			throw new ResourceNotFoundException("Could not find releases for project: " + projectId);
		}
		ProjectGeneration generation = project.findGeneration(name)
				.orElseThrow(() -> new ResourceNotFoundException("Could not find generation: " + name + " for project: " + projectId));
		GenerationMetadata generationMetadata = this.resourceAssembler.toResource(generation);
		return new Resource<>(generationMetadata);
	}

}
