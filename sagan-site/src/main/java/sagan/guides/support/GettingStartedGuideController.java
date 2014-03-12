package sagan.guides.support;

import sagan.support.nav.NavSection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@NavSection("guides")
@RequestMapping(value = "/guides/gs", method = { GET, HEAD })
class GettingStartedGuideController {

    private GettingStartedGuides guides;

    @Autowired
    public GettingStartedGuideController(GettingStartedGuides guides) {
        this.guides = guides;
    }

    @RequestMapping("/{guide}/")
    public String viewGuide(@PathVariable String guide, Model model) {
        model.addAttribute("guide", guides.find(guide));
        return "guides/gs/guide";
    }

    @RequestMapping("/{guide}/images/{image:[a-zA-Z0-9._-]+}")
    public ResponseEntity<byte[]> loadImage(@PathVariable String guide, @PathVariable String image) {
        return new ResponseEntity<>(guides.find(guide).getImage(image), HttpStatus.OK);
    }
}
