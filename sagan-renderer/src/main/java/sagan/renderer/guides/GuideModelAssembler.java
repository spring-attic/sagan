package sagan.renderer.guides;

import sagan.renderer.github.Repository;


import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

class GuideModelAssembler extends RepresentationModelAssemblerSupport<Repository, GuideModel> {

	GuideModelAssembler() {
		super(GuidesController.class, GuideModel.class);
	}

	@Override
	public GuideModel toModel(Repository repository) {
		GuideModel resource = new GuideModel(repository);
		resource.add(linkTo(methodOn(GuidesController.class)
				.showGuide(resource.getType().getSlug(), resource.getName())).withSelfRel());
		resource.add(linkTo(methodOn(GuidesController.class)
				.renderGuide(resource.getType().getSlug(), resource.getName())).withRel("content"));
		resource.add(linkTo(methodOn(GuidesController.class).listGuides()).withRel("guides"));
		return resource;
	}

}
