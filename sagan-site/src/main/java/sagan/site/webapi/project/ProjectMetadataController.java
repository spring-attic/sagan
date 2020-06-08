package sagan.site.webapi.project;

import java.util.List;

import sagan.site.projects.Project;
import sagan.site.projects.ProjectMetadataService;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expose {@link ProjectMetadata} resources.
 */
@RestController
@RequestMapping(path = "/api/projects", produces = MediaTypes.HAL_JSON_VALUE)
@ExposesResourceFor(ProjectMetadata.class)
public class ProjectMetadataController {

	private final ProjectMetadataService metadataService;

	private final ProjectMetadataAssembler resourceAssembler;

	public ProjectMetadataController(ProjectMetadataService metadataService, ProjectMetadataAssembler resourceAssembler) {
		this.metadataService = metadataService;
		this.resourceAssembler = resourceAssembler;
	}

	@GetMapping("")
	public Resources<ProjectMetadata> listProjects() {
		List<Project> projects = this.metadataService.fetchAllProjects();
		List<ProjectMetadata> projectMetadata = this.resourceAssembler.toResources(projects);
		Resources<ProjectMetadata> resources = new Resources<>(projectMetadata);
		return resources;
	}

	@GetMapping("/{id}")
	public Resource<ProjectMetadata> showProject(@PathVariable String id) {
		Project project = this.metadataService.fetchFullProject(id);
		ProjectMetadata projectMetadata = this.resourceAssembler.toResource(project);
		return new Resource<>(projectMetadata);
	}
}
