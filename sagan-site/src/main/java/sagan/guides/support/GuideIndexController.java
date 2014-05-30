package sagan.guides.support;

import sagan.support.nav.Navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Controller that handles requests for the index page for all guide docs at /guides.
 *
 * @see sagan.guides.support.GettingStartedGuideController
 * @see sagan.guides.support.TutorialController
 * @see sagan.guides.support.UnderstandingDocController
 */
@Controller
@Navigation(Section.GUIDES)
class GuideIndexController {

    private final GettingStartedGuides gsGuides;
    private final Tutorials tutorials;

    @Autowired
    public GuideIndexController(GettingStartedGuides gsGuides, Tutorials tutorials) {
        this.gsGuides = gsGuides;
        this.tutorials = tutorials;
    }

    @RequestMapping(value = "/guides", method = { GET, HEAD })
    public String viewIndex(Model model) {
        model.addAttribute("guides", gsGuides.findAll());
        model.addAttribute("tutorials", tutorials.findAll());
        return "guides/index";
    }
}
