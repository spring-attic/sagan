package sagan.guides.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sagan.guides.GettingStartedGuide;
import sagan.support.nav.Navigation;
import sagan.support.nav.Section;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.HEAD;

/**
 * Controller that handles requests for getting started guide docs at /guides/gs.
 *
 * @see sagan.guides.support.TutorialController
 * @see sagan.guides.support.UnderstandingDocController
 */
@Controller
@Navigation(Section.GUIDES)
@RequestMapping(value = "/guides/gs", method = { GET, HEAD })
class GettingStartedGuideController {

    private GettingStartedGuides guides;

    private String deployToPwsClientId;

    private String deployToPwsRedirectUrl;

    @Autowired
    public GettingStartedGuideController(GettingStartedGuides guides,
                                         @Value("${deploytopws.client_id}") String deployToPwsClientId,
                                         @Value("${deploytopws.redirect_url}") String deployToPwsRedirectUrl) {
        this.guides = guides;
        this.deployToPwsClientId = deployToPwsClientId;
        this.deployToPwsRedirectUrl = deployToPwsRedirectUrl;
    }

    @RequestMapping("/{guide}")
    public String viewGuide(@PathVariable String guide, Model model) {
        GettingStartedGuide gsGuide = guides.find(guide);
        model.addAttribute("guide", gsGuide);
        model.addAttribute("description", "this guide is designed to get you productive as quickly as " +
                "possible and using the latest Spring project releases and techniques as recommended by the Spring team");
        model.addAttribute("deployToPwsClientId", this.deployToPwsClientId);
        model.addAttribute("deployToPwsRedirectUrl", this.deployToPwsRedirectUrl);
        return "guides/gs/guide";
    }

    @RequestMapping("/{guide}/images/{image:[a-zA-Z0-9._-]+}")
    public ResponseEntity<byte[]> loadImage(@PathVariable String guide, @PathVariable String image) {
        return ResponseEntity.ok(guides.loadImage(guides.find(guide), image));
    }
}
