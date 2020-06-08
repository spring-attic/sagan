package sagan.site.webapi.repository;

import java.util.Arrays;
import java.util.List;

import sagan.site.projects.Repository;
import sagan.support.ResourceNotFoundException;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@RequestMapping(path = "/api/repositories", produces = MediaTypes.HAL_JSON_VALUE)
@ExposesResourceFor(RepositoryMetadata.class)
public class RepositoryMetadataController {

	private final RepositoryMetadataAssembler resourceAssembler;

	public RepositoryMetadataController(RepositoryMetadataAssembler resourceAssembler) {
		this.resourceAssembler = resourceAssembler;
	}

	@GetMapping("")
	public Resources<RepositoryMetadata> listRepositories() {
		List<RepositoryMetadata> repositories = this.resourceAssembler.toResources(Arrays.asList(Repository.values()));
		return new Resources<>(repositories);
	}

	@GetMapping("/{id}")
	public Resource<RepositoryMetadata> showRepository(@PathVariable String id) {
		Repository repository = Arrays.stream(Repository.values())
				.filter(r -> r.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("No artifact repository found with id: " + id));
		return new Resource<>(this.resourceAssembler.toResource(repository));
	}
}
