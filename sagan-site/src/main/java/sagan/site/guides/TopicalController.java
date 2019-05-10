package sagan.site.guides;

import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that handles requests for tutorial docs at /guides/tutorials.
 *
 * @see GettingStartedGuideController
 */
@Controller
@Navigation(Section.GUIDES)
@RequestMapping("/guides/topicals")
class TopicalController {

	private Topicals topicals;

	@Autowired
	public TopicalController(Topicals topicals) {
		this.topicals = topicals;
	}

	@GetMapping("/{topical}")
	public String viewTutorial(@PathVariable String topical, Model model) {
		model.addAttribute("guide", this.topicals.findByName(topical).get());
		model.addAttribute("description",
				"this topical is designed to be read and comprehended in under an hour, it provides broad "
						+ "coverage of a topic that is possibly nuanced or requires deeper understanding than you would get from a getting started guide");
		return "guides/gs/guide";
	}

	@GetMapping("/{topical}/images/{image:[a-zA-Z0-9._-]+}")
	public ResponseEntity<byte[]> loadImage(@PathVariable String topical, @PathVariable String image) {
		return this.topicals.findByName(topical)
				.flatMap(guide -> guide.getImageContent(image))
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

}
