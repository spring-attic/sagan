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
    private final Topicals topicals;

    @Autowired
    public GuideIndexController(GettingStartedGuides gsGuides, Tutorials tutorials, Topicals topicals) {
        this.gsGuides = gsGuides;
        this.tutorials = tutorials;
        this.topicals = topicals;
    }

    @RequestMapping(value = "/guides", method = { GET, HEAD })
    public String viewIndex(Model model) {
        model.addAttribute("guides", gsGuides.findAllMetadata());
        model.addAttribute("tutorials", tutorials.findAllMetadata());
        model.addAttribute("topicals", topicals.findAllMetadata());
        return "guides/index";
    }
}
