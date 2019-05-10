package sagan.renderer;

import sagan.renderer.guides.GuidesController;
import sagan.renderer.markup.MarkupController;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Lists all resources at the root of the application
 */
@RestController
public class IndexController {

	@GetMapping(path = "/", produces = MediaTypes.HAL_JSON_VALUE)
	public ResourceSupport index() {
		ResourceSupport resource = new ResourceSupport();
		resource.add(linkTo(methodOn(MarkupController.class).renderMarkup(MediaType.TEXT_MARKDOWN, ""))
				.withRel("markup"));
		resource.add(linkTo(methodOn(GuidesController.class).listGuides()).withRel("guides"));
		return resource;
	}
}
