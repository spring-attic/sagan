package sagan.site.webapi.repository;

import java.util.Arrays;

import sagan.site.projects.Repository;
import sagan.support.ResourceNotFoundException;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.ExposesResourceFor;
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
	public CollectionModel<RepositoryMetadata> listRepositories() {
		return this.resourceAssembler.toCollectionModel(Arrays.asList(Repository.values()));
	}

	@GetMapping("/{id}")
	public EntityModel<RepositoryMetadata> showRepository(@PathVariable String id) {
		Repository repository = Arrays.stream(Repository.values())
				.filter(r -> r.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("No artifact repository found with id: " + id));
		return EntityModel.of(this.resourceAssembler.toModel(repository));
	}
}
