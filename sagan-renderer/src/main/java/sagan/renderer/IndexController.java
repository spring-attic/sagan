package sagan.renderer;

import sagan.renderer.guides.GuidesController;
import sagan.renderer.markup.MarkupController;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


/**
 * Lists all resources at the root of the application
 */
@RestController
public class IndexController {

	@GetMapping(path = "/", produces = MediaTypes.HAL_JSON_VALUE)
	public RepresentationModel index() {
		RepresentationModel resource = new RepresentationModel();
		resource.add(linkTo(methodOn(MarkupController.class).renderMarkup(MediaType.TEXT_MARKDOWN, ""))
				.withRel("markup"));
		resource.add(linkTo(methodOn(GuidesController.class).listGuides()).withRel("guides"));
		return resource;
	}
}
