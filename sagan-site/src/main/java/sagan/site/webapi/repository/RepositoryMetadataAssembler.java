package sagan.site.webapi.repository;

import sagan.site.projects.Repository;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 *
 */
@Component
class RepositoryMetadataAssembler extends ResourceAssemblerSupport<Repository, RepositoryMetadata> {

	public RepositoryMetadataAssembler() {
		super(RepositoryMetadataController.class, RepositoryMetadata.class);
	}

	@Override
	public RepositoryMetadata toResource(Repository repository) {
		RepositoryMetadata repositoryMetadata = new RepositoryMetadata(repository.getId(), repository.getName(), repository.getUrl(), repository.isSnapshotsEnabled());
		repositoryMetadata.add(linkTo(methodOn(RepositoryMetadataController.class).showRepository(repository.getId())).withSelfRel());
		return repositoryMetadata;
	}
}
