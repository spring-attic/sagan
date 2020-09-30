package sagan.site.webapi.project;

import org.modelmapper.ModelMapper;
import sagan.site.projects.Project;
import sagan.site.webapi.generation.GenerationMetadataController;
import sagan.site.webapi.release.ReleaseMetadataController;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Map {@link Project} to {@link ProjectMetadata}, while also adding links to related resources.
 */
@Component
class ProjectMetadataAssembler extends RepresentationModelAssemblerSupport<Project, ProjectMetadata> {

	private final EntityLinks entityLinks;

	private final ModelMapper modelMapper;

	public ProjectMetadataAssembler(EntityLinks entityLinks, ModelMapper modelMapper) {
		super(ProjectMetadataController.class, ProjectMetadata.class);
		this.entityLinks = entityLinks;
		this.modelMapper = modelMapper;
		this.modelMapper.createTypeMap(Project.class, ProjectMetadata.class)
				.addMapping(Project::getRepoUrl, ProjectMetadata::setRepositoryUrl)
				.addMapping(Project::getId, ProjectMetadata::setSlug);
	}

	@Override
	public ProjectMetadata toModel(Project project) {
		ProjectMetadata projectMetadata = this.modelMapper.map(project, ProjectMetadata.class);
		Link selfLink = this.entityLinks.linkToItemResource(ProjectMetadata.class, project.getId()).withSelfRel();
		projectMetadata.add(linkTo(methodOn(ReleaseMetadataController.class).listReleases(project.getId())).withRel("releases"),
				linkTo(methodOn(GenerationMetadataController.class).listGenerations(project.getId())).withRel("generations"));
		return projectMetadata;
	}
}
