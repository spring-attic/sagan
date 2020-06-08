package sagan.site.webapi.project;

import org.modelmapper.ModelMapper;
import sagan.site.projects.Project;
import sagan.site.webapi.generation.GenerationMetadataController;
import sagan.site.webapi.release.ReleaseMetadataController;

import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Map {@link Project} to {@link ProjectMetadata}, while also adding links to related resources.
 */
@Component
class ProjectMetadataAssembler extends ResourceAssemblerSupport<Project, ProjectMetadata> {

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
	public ProjectMetadata toResource(Project project) {
		ProjectMetadata projectMetadata = this.modelMapper.map(project, ProjectMetadata.class);
		Link selfLink = this.entityLinks.linkToSingleResource(ProjectMetadata.class, project.getId()).withSelfRel();
		Link releasesLink = linkTo(methodOn(ReleaseMetadataController.class).listReleases(project.getId())).withRel("releases");
		Link generationsLink = linkTo(methodOn(GenerationMetadataController.class).listGenerations(project.getId())).withRel("generations");
		projectMetadata.add(selfLink, releasesLink, generationsLink);
		if (project.getParentId() != null) {
			Link parentLink = this.entityLinks.linkToSingleResource(ProjectMetadata.class, project.getParentId()).withRel("parent");
			projectMetadata.add(parentLink);
		}
		return projectMetadata;
	}
}
