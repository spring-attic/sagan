package sagan.site.webapi;

import sagan.site.webapi.project.ProjectMetadataController;
import sagan.site.webapi.repository.RepositoryMetadataController;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Lists all resources at the root of the Web API
 */
@RestController
class IndexController {

	@GetMapping(path = "/api", produces = MediaTypes.HAL_JSON_VALUE)
	public ResourceSupport index() {
		ResourceSupport resource = new ResourceSupport();
		resource.add(linkTo(methodOn(ProjectMetadataController.class).listProjects()).withRel("projects"));
		resource.add(linkTo(methodOn(RepositoryMetadataController.class).listRepositories()).withRel("repositories"));
		return resource;
	}
}
