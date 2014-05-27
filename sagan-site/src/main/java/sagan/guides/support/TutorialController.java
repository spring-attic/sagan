package sagan.guides.support;

import sagan.support.nav.Navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles requests for tutorial docs at /guides/tutorials.
 *
 * @see sagan.guides.support.GettingStartedGuideController
 * @see sagan.guides.support.UnderstandingDocController
 */
@Controller
@Navigation(Section.GUIDES)
@RequestMapping(value = "/guides/tutorials", method = { GET, HEAD })
class TutorialController {

    private Tutorials tutorials;

    @Autowired
    public TutorialController(Tutorials tutorials) {
        this.tutorials = tutorials;
    }

    @RequestMapping("/{tutorial}")
    public String viewTutorial(@PathVariable String tutorial, Model model) {
        model.addAttribute("tutorialId", tutorial);
        model.addAttribute("tutorial", tutorials.find(tutorial));
        return "guides/tutorial/show";
    }

    @RequestMapping("/{tutorial}/{page:[1-9][0-9]*}")
    public String viewTutorialPage(@PathVariable String tutorial, @PathVariable Integer page, Model model) {
        model.addAttribute("tutorialId", tutorial);
        model.addAttribute("tutorial", tutorials.findByPage(tutorial, page));
        return "guides/tutorial/show";
    }

    @RequestMapping("/{tutorial}/images/{image:[a-zA-Z0-9._-]+}")
    public ResponseEntity<byte[]> loadImage(@PathVariable String tutorial, @PathVariable String image) {
        return new ResponseEntity<>(tutorials.find(tutorial).getImage(image), HttpStatus.OK);
    }

}
