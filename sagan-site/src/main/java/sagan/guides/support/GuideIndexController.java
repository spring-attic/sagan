package sagan.guides.support;

import sagan.util.web.NavSection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NavSection("guides")
class GuideIndexController {

    private final Tutorials tutorials;
    private final GettingStartedGuides gsGuides;

    @Autowired
    public GuideIndexController(Tutorials tutorials, GettingStartedGuides gsGuides) {
        this.tutorials = tutorials;
        this.gsGuides = gsGuides;
    }

    @RequestMapping(value = "/guides", method = { GET, HEAD })
    public String viewIndex(Model model) {
        model.addAttribute("guides", gsGuides.findAll());
        model.addAttribute("tutorials", tutorials.findAll());
        return "guides/index";
    }
}
