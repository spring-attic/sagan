package sagan.site.webapi.generation;

import org.modelmapper.ModelMapper;
import sagan.site.projects.ProjectGeneration;
import sagan.site.webapi.project.ProjectMetadataController;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * @author Brian Clozel
 */
@Component
class GenerationMetadataAssembler extends ResourceAssemblerSupport<ProjectGeneration, GenerationMetadata> {

	private final ModelMapper modelMapper;

	public GenerationMetadataAssembler(ModelMapper modelMapper) {
		super(GenerationMetadataController.class, GenerationMetadata.class);
		this.modelMapper = modelMapper;
		this.modelMapper.getConfiguration().setAmbiguityIgnored(true);
		this.modelMapper.createTypeMap(ProjectGeneration.class, GenerationMetadata.class)
				.addMapping(ProjectGeneration::ossSupportEndDate, GenerationMetadata::setOssSupportEndDate)
				.addMapping(ProjectGeneration::commercialSupportEndDate, GenerationMetadata::setCommercialSupportEndDate);
	}

	@Override
	public GenerationMetadata toResource(ProjectGeneration entity) {
		GenerationMetadata generation = this.modelMapper.map(entity, GenerationMetadata.class);
		Link selfLink = linkTo(methodOn(GenerationMetadataController.class).showRelease(entity.getProject().getId(), entity.getName())).withSelfRel();
		generation.add(selfLink);
		generation.add(linkTo(methodOn(ProjectMetadataController.class).showProject(entity.getProject().getId())).withRel("project"));
		return generation;
	}
}
