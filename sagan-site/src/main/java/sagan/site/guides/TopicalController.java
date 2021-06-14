package sagan.site.guides;

import java.util.Arrays;
import java.util.Optional;

import sagan.site.support.ResourceNotFoundException;
import sagan.site.support.nav.Navigation;
import sagan.site.support.nav.Section;

import org.springframework.beans.factory.annotation.Autowired;
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
		boolean knownTopical = Arrays.stream(this.topicals.findAll()).anyMatch(header -> header.getName().equals(topical));
		if (knownTopical) {
			Topical topicalGuide = this.topicals.findByName(topical);
			model.addAttribute("guide", topicalGuide);
			model.addAttribute("description",
					"this topical is designed to be read and comprehended in under an hour, it provides broad "
							+ "coverage of a topic that is possibly nuanced or requires deeper understanding than you would get from a getting started guide");
			return "guides/gs/guide";
		}
		throw new ResourceNotFoundException("Missing topical guide: '" + topical + "'");
	}

}
