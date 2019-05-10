package sagan.renderer.guides;

import java.util.List;
import java.util.stream.Collectors;

import sagan.renderer.RendererProperties;
import sagan.renderer.github.GithubClient;
import sagan.renderer.github.GithubResourceNotFoundException;
import sagan.renderer.github.Repository;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * API for listing guides repositories and rendering them as {@link GuideContentResource}
 */
@RestController
@RequestMapping(path = "/guides", produces = MediaTypes.HAL_JSON_VALUE)
public class GuidesController {

	private final GuideRenderer guideRenderer;

	private final GithubClient githubClient;

	private final RendererProperties properties;

	private final GuideResourceAssembler guideAssembler = new GuideResourceAssembler();

	public GuidesController(GuideRenderer guideRenderer, GithubClient github,
			RendererProperties properties) {
		this.guideRenderer = guideRenderer;
		this.githubClient = github;
		this.properties = properties;
	}

	@ExceptionHandler(GithubResourceNotFoundException.class)
	public ResponseEntity resourceNotFound() {
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/")
	public Resources<GuideResource> listGuides() {
		List<GuideResource> guideResources = this.guideAssembler
				.toResources(this.githubClient.fetchOrgRepositories(properties.getGuides().getOrganization()))
				.stream().filter(guide -> !guide.getType().equals(GuideType.UNKNOWN))
				.collect(Collectors.toList());
		Resources<GuideResource> resources = new Resources<>(guideResources);

		for (GuideType type : GuideType.values()) {
			if (!GuideType.UNKNOWN.equals(type)) {
				resources.add(linkTo(methodOn(GuidesController.class).showGuide(type.getSlug(), null))
						.withRel(type.getSlug()));
			}
		}
		return resources;
	}

	@GetMapping("/{type}/{guide}")
	public ResponseEntity<GuideResource> showGuide(@PathVariable String type, @PathVariable String guide) {
		GuideType guideType = GuideType.fromSlug(type);
		if (GuideType.UNKNOWN.equals(guideType)) {
			return ResponseEntity.notFound().build();
		}
		Repository repository = this.githubClient.fetchOrgRepository(properties.getGuides().getOrganization(),
				guideType.getPrefix() + guide);
		GuideResource guideResource = this.guideAssembler.toResource(repository);
		if (guideResource.getType().equals(GuideType.UNKNOWN)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(guideResource);
	}

	@GetMapping("/{type}/{guide}/content")
	public ResponseEntity<GuideContentResource> renderGuide(@PathVariable String type, @PathVariable String guide) {
		GuideType guideType = GuideType.fromSlug(type);
		if (GuideType.UNKNOWN.equals(guideType)) {
			return ResponseEntity.notFound().build();
		}
		GuideContentResource guideContentResource = this.guideRenderer.render(guideType, guide);
		guideContentResource.add(linkTo(methodOn(GuidesController.class).renderGuide(guideType.getSlug(), guide)).withSelfRel());
		guideContentResource.add(linkTo(methodOn(GuidesController.class).showGuide(guideType.getSlug(), guide)).withRel("guide"));
		return ResponseEntity.ok(guideContentResource);
	}

}