package sagan.site.webapi.generation;

import org.modelmapper.ModelMapper;
import sagan.site.projects.ProjectGeneration;
import sagan.site.webapi.project.ProjectMetadataController;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * @author Brian Clozel
 */
@Component
class GenerationMetadataAssembler extends RepresentationModelAssemblerSupport<ProjectGeneration, GenerationMetadata> {

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
	public GenerationMetadata toModel(ProjectGeneration entity) {
		GenerationMetadata generation = this.modelMapper.map(entity, GenerationMetadata.class);
		Link selfLink = linkTo(methodOn(GenerationMetadataController.class).showRelease(entity.getProject().getId(), entity.getName())).withSelfRel();
		generation.add(selfLink);
		generation.add(linkTo(methodOn(ProjectMetadataController.class).showProject(entity.getProject().getId())).withRel("project"));
		return generation;
	}
}
