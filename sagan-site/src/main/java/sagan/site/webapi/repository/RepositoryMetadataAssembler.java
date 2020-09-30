package sagan.site.webapi.repository;

import sagan.site.projects.Repository;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 *
 */
@Component
class RepositoryMetadataAssembler extends RepresentationModelAssemblerSupport<Repository, RepositoryMetadata> {

	public RepositoryMetadataAssembler() {
		super(RepositoryMetadataController.class, RepositoryMetadata.class);
	}

	@Override
	public RepositoryMetadata toModel(Repository repository) {
		RepositoryMetadata repositoryMetadata = new RepositoryMetadata(repository.getId(), repository.getName(), repository.getUrl(), repository.isSnapshotsEnabled());
		repositoryMetadata.add(linkTo(methodOn(RepositoryMetadataController.class).showRepository(repository.getId())).withSelfRel());
		return repositoryMetadata;
	}

}
