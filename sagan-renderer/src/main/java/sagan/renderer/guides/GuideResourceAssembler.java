package sagan.renderer.guides;

import sagan.renderer.github.Repository;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

class GuideResourceAssembler extends ResourceAssemblerSupport<Repository, GuideResource> {

	GuideResourceAssembler() {
		super(GuidesController.class, GuideResource.class);
	}

	@Override
	public GuideResource toResource(Repository repository) {
		GuideResource resource = new GuideResource(repository);
		resource.add(linkTo(methodOn(GuidesController.class)
				.showGuide(resource.getType().getSlug(), resource.getName())).withSelfRel());
		resource.add(linkTo(methodOn(GuidesController.class)
				.renderGuide(resource.getType().getSlug(), resource.getName())).withRel("content"));
		resource.add(linkTo(methodOn(GuidesController.class).listGuides()).withRel("guides"));
		return resource;
	}

}
