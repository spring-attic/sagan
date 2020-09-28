package sagan.renderer.guides;

import java.util.List;
import java.util.stream.Collectors;

import sagan.renderer.RendererProperties;
import sagan.renderer.github.GithubClient;
import sagan.renderer.github.GithubResourceNotFoundException;
import sagan.renderer.github.Repository;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * API for listing guides repositories and rendering them as {@link GuideContentModel}
 */
@RestController
@RequestMapping(path = "/guides", produces = MediaTypes.HAL_JSON_VALUE)
public class GuidesController {

	private final GuideRenderer guideRenderer;

	private final GithubClient githubClient;

	private final RendererProperties properties;

	private final GuideModelAssembler guideAssembler = new GuideModelAssembler();

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
	public CollectionModel<GuideModel> listGuides() {
		List<GuideModel> guideModels = this.guideAssembler
				.toCollectionModel(this.githubClient.fetchOrgRepositories(properties.getGuides().getOrganization()))
				.getContent()
				.stream().filter(guide -> !guide.getType().equals(GuideType.UNKNOWN))
				.collect(Collectors.toList());
		CollectionModel<GuideModel> resources = CollectionModel.of(guideModels);

		for (GuideType type : GuideType.values()) {
			if (!GuideType.UNKNOWN.equals(type)) {
				resources.add(linkTo(methodOn(GuidesController.class).showGuide(type.getSlug(), null))
						.withRel(type.getSlug()));
			}
		}
		return resources;
	}

	@GetMapping("/{type}/{guide}")
	public ResponseEntity<GuideModel> showGuide(@PathVariable String type, @PathVariable String guide) {
		GuideType guideType = GuideType.fromSlug(type);
		if (GuideType.UNKNOWN.equals(guideType)) {
			return ResponseEntity.notFound().build();
		}
		Repository repository = this.githubClient.fetchOrgRepository(properties.getGuides().getOrganization(),
				guideType.getPrefix() + guide);
		GuideModel guideModel = this.guideAssembler.toModel(repository);
		if (guideModel.getType().equals(GuideType.UNKNOWN)) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(guideModel);
	}

	@GetMapping("/{type}/{guide}/content")
	public ResponseEntity<GuideContentModel> renderGuide(@PathVariable String type, @PathVariable String guide) {
		GuideType guideType = GuideType.fromSlug(type);
		if (GuideType.UNKNOWN.equals(guideType)) {
			return ResponseEntity.notFound().build();
		}
		GuideContentModel guideContentModel = this.guideRenderer.render(guideType, guide);
		guideContentModel.add(linkTo(methodOn(GuidesController.class).renderGuide(guideType.getSlug(), guide)).withSelfRel());
		guideContentModel.add(linkTo(methodOn(GuidesController.class).showGuide(guideType.getSlug(), guide)).withRel("guide"));
		return ResponseEntity.ok(guideContentModel);
	}

}