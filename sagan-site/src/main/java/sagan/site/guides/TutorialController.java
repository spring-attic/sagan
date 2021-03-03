package sagan.site.guides;

import java.util.Optional;

import sagan.site.support.ResourceNotFoundException;
import sagan.site.support.nav.Navigation;
import sagan.site.support.nav.Section;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that handles requests for tutorial docs at /guides/tutorials.
 *
 * @see GettingStartedGuideController
 * @see TopicalController
 */
@Controller
@Navigation(Section.GUIDES)
@RequestMapping("/guides/tutorials")
class TutorialController {

    private Tutorials tutorials;

    @Autowired
    public TutorialController(Tutorials tutorials) {
        this.tutorials = tutorials;
    }

    @RequestMapping("/{tutorial}")
    public String viewTutorial(@PathVariable String tutorial, Model model) {
		Optional<Tutorial> tutorialGuide = this.tutorials.findByName(tutorial);
		if (tutorialGuide.isPresent()) {
			model.addAttribute("guide", tutorialGuide.get());
			model.addAttribute("description", "this tutorial is designed to be completed in 2-3 hours, it provides deeper," +
					" in-context explorations of enterprise application development topics, leaving you ready to implement real-world solutions.");
			return "guides/gs/guide";
		}
		throw new ResourceNotFoundException("Missing tutorial guide: '" + tutorial + "'");
    }

}
