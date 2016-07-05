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
@RequestMapping(value = "/guides/topicals", method = { GET, HEAD })
class TopicalController {

    private Topicals topicals;

    @Autowired
    public TopicalController(Topicals topicals) {
        this.topicals = topicals;
    }

    @RequestMapping("/{topical}")
    public String viewTutorial(@PathVariable String topical, Model model) {
        model.addAttribute("guide", topicals.find(topical));
        model.addAttribute("description",
                "this topical is designed to be read and comprehended in under an hour, it provides broad "
                + "coverage of a topic that is possibly nuanced or requires deeper understanding than you would get from a getting started guide");
        return "guides/gs/guide";
    }

    @RequestMapping("/{topical}/images/{image:[a-zA-Z0-9._-]+}")
    public ResponseEntity<byte[]> loadImage(@PathVariable String topical, @PathVariable String image) {
        return ResponseEntity.ok(topicals.loadImage(topicals.findMetadata(topical), image));
    }

}
